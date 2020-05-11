#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.core.management.base import BaseCommand
from django.db import transaction
from django.db.models import Q

from bldcontrol.bbcontroller import getBuildEnvironmentController
from bldcontrol.models import BuildRequest, BuildEnvironment
from bldcontrol.models import BRError, BRVariable

from orm.models import Build, LogMessage, Target

import logging
import traceback
import signal
import os

logger = logging.getLogger("toaster")


class Command(BaseCommand):
    args = ""
    help = "Schedules and executes build requests as possible. "\
           "Does not return (interrupt with Ctrl-C)"

    @transaction.atomic
    def _selectBuildEnvironment(self):
        bec = getBuildEnvironmentController(lock=BuildEnvironment.LOCK_FREE)
        bec.be.lock = BuildEnvironment.LOCK_LOCK
        bec.be.save()
        return bec

    @transaction.atomic
    def _selectBuildRequest(self):
        br = BuildRequest.objects.filter(state=BuildRequest.REQ_QUEUED).first()
        return br

    def schedule(self):
        try:
            # select the build environment and the request to build
            br = self._selectBuildRequest()
            if br:
                br.state = BuildRequest.REQ_INPROGRESS
                br.save()
            else:
                return

            try:
                bec = self._selectBuildEnvironment()
            except IndexError as e:
                # we could not find a BEC; postpone the BR
                br.state = BuildRequest.REQ_QUEUED
                br.save()
                logger.debug("runbuilds: No build env (%s)" % e)
                return

            logger.info("runbuilds: starting build %s, environment %s" %
                        (br, bec.be))

            # let the build request know where it is being executed
            br.environment = bec.be
            br.save()

            # this triggers an async build
            bec.triggerBuild(br.brbitbake, br.brlayer_set.all(),
                             br.brvariable_set.all(), br.brtarget_set.all(),
                             "%d:%d" % (br.pk, bec.be.pk))

        except Exception as e:
            logger.error("runbuilds: Error launching build %s" % e)
            traceback.print_exc()
            if "[Errno 111] Connection refused" in str(e):
                # Connection refused, read toaster_server.out
                errmsg = bec.readServerLogFile()
            else:
                errmsg = str(e)

            BRError.objects.create(req=br, errtype=str(type(e)), errmsg=errmsg,
                                   traceback=traceback.format_exc())
            br.state = BuildRequest.REQ_FAILED
            br.save()
            bec.be.lock = BuildEnvironment.LOCK_FREE
            bec.be.save()
            # Cancel the pending build and report the exception to the UI
            log_object = LogMessage.objects.create(
                            build = br.build,
                            level = LogMessage.EXCEPTION,
                            message = errmsg)
            log_object.save()
            br.build.outcome = Build.FAILED
            br.build.save()

    def archive(self):
        for br in BuildRequest.objects.filter(state=BuildRequest.REQ_ARCHIVE):
            if br.build is None:
                br.state = BuildRequest.REQ_FAILED
            else:
                br.state = BuildRequest.REQ_COMPLETED
            br.save()

    def cleanup(self):
        from django.utils import timezone
        from datetime import timedelta
        # environments locked for more than 30 seconds
        # they should be unlocked
        BuildEnvironment.objects.filter(
            Q(buildrequest__state__in=[BuildRequest.REQ_FAILED,
                                       BuildRequest.REQ_COMPLETED,
                                       BuildRequest.REQ_CANCELLING]) &
            Q(lock=BuildEnvironment.LOCK_LOCK) &
            Q(updated__lt=timezone.now() - timedelta(seconds=30))
        ).update(lock=BuildEnvironment.LOCK_FREE)

        # update all Builds that were in progress and failed to start
        for br in BuildRequest.objects.filter(
                state=BuildRequest.REQ_FAILED,
                build__outcome=Build.IN_PROGRESS):
            # transpose the launch errors in ToasterExceptions
            br.build.outcome = Build.FAILED
            for brerror in br.brerror_set.all():
                logger.debug("Saving error %s" % brerror)
                LogMessage.objects.create(build=br.build,
                                          level=LogMessage.EXCEPTION,
                                          message=brerror.errmsg)
            br.build.save()

            # we don't have a true build object here; hence, toasterui
            # didn't have a change to release the BE lock
            br.environment.lock = BuildEnvironment.LOCK_FREE
            br.environment.save()

        # update all BuildRequests without a build created
        for br in BuildRequest.objects.filter(build=None):
            br.build = Build.objects.create(project=br.project,
                                            completed_on=br.updated,
                                            started_on=br.created)
            br.build.outcome = Build.FAILED
            try:
                br.build.machine = br.brvariable_set.get(name='MACHINE').value
            except BRVariable.DoesNotExist:
                pass
            br.save()
            # transpose target information
            for brtarget in br.brtarget_set.all():
                Target.objects.create(build=br.build,
                                      target=brtarget.target,
                                      task=brtarget.task)
            # transpose the launch errors in ToasterExceptions
            for brerror in br.brerror_set.all():
                LogMessage.objects.create(build=br.build,
                                          level=LogMessage.EXCEPTION,
                                          message=brerror.errmsg)

            br.build.save()

        # Make sure the LOCK is removed for builds which have been fully
        # cancelled
        for br in BuildRequest.objects.filter(
                      Q(build__outcome=Build.CANCELLED) &
                      Q(state=BuildRequest.REQ_CANCELLING) &
                      ~Q(environment=None)):
            br.environment.lock = BuildEnvironment.LOCK_FREE
            br.environment.save()

    def runbuild(self):
        try:
            self.cleanup()
        except Exception as e:
            logger.warning("runbuilds: cleanup exception %s" % str(e))

        try:
            self.archive()
        except Exception as e:
            logger.warning("runbuilds: archive exception %s" % str(e))

        try:
            self.schedule()
        except Exception as e:
            logger.warning("runbuilds: schedule exception %s" % str(e))

    def handle(self, **options):
        pidfile_path = os.path.join(os.environ.get("BUILDDIR", "."),
                                    ".runbuilds.pid")

        with open(pidfile_path, 'w') as pidfile:
            pidfile.write("%s" % os.getpid())

        self.runbuild()

        signal.signal(signal.SIGUSR1, lambda sig, frame: None)

        while True:
            signal.pause()
            self.runbuild()
