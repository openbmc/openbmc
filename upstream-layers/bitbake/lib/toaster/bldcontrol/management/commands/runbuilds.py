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

    # Test to see if a build pre-maturely died due to a bitbake crash
    def check_dead_builds(self):
        do_cleanup = False
        try:
            for br in BuildRequest.objects.filter(state=BuildRequest.REQ_INPROGRESS):
                # Get the build directory
                if br.project.builddir:
                    builddir =  br.project.builddir
                else:
                    builddir = '%s-toaster-%d' % (br.environment.builddir,br.project.id)
                # Check log to see if there is a recent traceback
                toaster_ui_log = os.path.join(builddir, 'toaster_ui.log')
                test_file = os.path.join(builddir, '._toaster_check.txt')
                os.system("tail -n 50 %s > %s" % (os.path.join(builddir, 'toaster_ui.log'),test_file))
                traceback_text = ''
                is_traceback = False
                with open(test_file,'r') as test_file_fd:
                    test_file_tail = test_file_fd.readlines()
                    for line in test_file_tail:
                        if line.startswith('Traceback (most recent call last):'):
                            traceback_text = line
                            is_traceback = True
                        elif line.startswith('NOTE: ToasterUI waiting for events'):
                            # Ignore any traceback before new build start
                            traceback_text = ''
                            is_traceback = False
                        elif line.startswith('Note: Toaster traceback auto-stop'):
                            # Ignore any traceback before this previous traceback catch
                            traceback_text = ''
                            is_traceback = False
                        elif is_traceback:
                            traceback_text += line
                # Test the results
                is_stop = False
                if is_traceback:
                    # Found a traceback
                    errtype = 'Bitbake crash'
                    errmsg = 'Bitbake crash\n' + traceback_text
                    state = BuildRequest.REQ_FAILED
                    # Clean up bitbake files
                    bitbake_lock = os.path.join(builddir, 'bitbake.lock')
                    if os.path.isfile(bitbake_lock):
                        os.remove(bitbake_lock)
                    bitbake_sock = os.path.join(builddir, 'bitbake.sock')
                    if os.path.isfile(bitbake_sock):
                        os.remove(bitbake_sock)
                    if os.path.isfile(test_file):
                        os.remove(test_file)
                    # Add note to ignore this traceback on next check
                    os.system('echo "Note: Toaster traceback auto-stop" >> %s' % toaster_ui_log)
                    is_stop = True
                # Add more tests here
                #elif ...
                # Stop the build request?
                if is_stop:
                    brerror = BRError(
                        req = br,
                        errtype = errtype,
                        errmsg = errmsg,
                        traceback = traceback_text,
                        )
                    brerror.save()
                    br.state = state
                    br.save()
                    do_cleanup = True
            # Do cleanup
            if do_cleanup:
                self.cleanup()
        except Exception as e:
            logger.error("runbuilds: Error in check_dead_builds %s" % e)

    def handle(self, **options):
        pidfile_path = os.path.join(os.environ.get("BUILDDIR", "."),
                                    ".runbuilds.pid")

        with open(pidfile_path, 'w') as pidfile:
            pidfile.write("%s" % os.getpid())

        # Clean up any stale/failed builds from previous Toaster run
        self.runbuild()

        signal.signal(signal.SIGUSR1, lambda sig, frame: None)

        while True:
            sigset = signal.sigtimedwait([signal.SIGUSR1], 5)
            if sigset:
                for sig in sigset:
                    # Consume each captured pending event
                    self.runbuild()
            else:
                # Check for build exceptions
                self.check_dead_builds()

