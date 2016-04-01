from django.core.management.base import NoArgsCommand, CommandError
from django.db import transaction
from orm.models import Build, ToasterSetting, LogMessage, Target
from bldcontrol.bbcontroller import getBuildEnvironmentController, ShellCmdException, BuildSetupException
from bldcontrol.models import BuildRequest, BuildEnvironment, BRError, BRVariable
import os
import logging
import time

logger = logging.getLogger("ToasterScheduler")

class Command(NoArgsCommand):
    args    = ""
    help    = "Schedules and executes build requests as possible. Does not return (interrupt with Ctrl-C)"


    @transaction.commit_on_success
    def _selectBuildEnvironment(self):
        bec = getBuildEnvironmentController(lock = BuildEnvironment.LOCK_FREE)
        bec.be.lock = BuildEnvironment.LOCK_LOCK
        bec.be.save()
        return bec

    @transaction.commit_on_success
    def _selectBuildRequest(self):
        br = BuildRequest.objects.filter(state = BuildRequest.REQ_QUEUED).order_by('pk')[0]
        br.state = BuildRequest.REQ_INPROGRESS
        br.save()
        return br

    def schedule(self):
        import traceback
        try:
            br = None
            try:
                # select the build environment and the request to build
                br = self._selectBuildRequest()
            except IndexError as e:
                #logger.debug("runbuilds: No build request")
                return
            try:
                bec = self._selectBuildEnvironment()
            except IndexError as e:
                # we could not find a BEC; postpone the BR
                br.state = BuildRequest.REQ_QUEUED
                br.save()
                logger.debug("runbuilds: No build env")
                return

            logger.debug("runbuilds: starting build %s, environment %s" % (br, bec.be))

            # write the build identification variable
            BRVariable.objects.create(req = br, name="TOASTER_BRBE", value="%d:%d" % (br.pk, bec.be.pk))

            # let the build request know where it is being executed
            br.environment = bec.be
            br.save()

            # this triggers an async build
            bec.triggerBuild(br.brbitbake_set.all(), br.brlayer_set.all(), br.brvariable_set.all(), br.brtarget_set.all())

        except Exception as e:
            logger.error("runbuilds: Error launching build %s" % e)
            traceback.print_exc(e)
            if "[Errno 111] Connection refused" in str(e):
                # Connection refused, read toaster_server.out
                errmsg = bec.readServerLogFile()
            else:
                errmsg = str(e)

            BRError.objects.create(req = br,
                    errtype = str(type(e)),
                    errmsg = errmsg,
                    traceback = traceback.format_exc(e))
            br.state = BuildRequest.REQ_FAILED
            br.save()
            bec.be.lock = BuildEnvironment.LOCK_FREE
            bec.be.save()

    def archive(self):
        for br in BuildRequest.objects.filter(state = BuildRequest.REQ_ARCHIVE):
            if br.build == None:
                br.state = BuildRequest.REQ_FAILED
            else:
                br.state = BuildRequest.REQ_COMPLETED
            br.save()

    def cleanup(self):
        from django.utils import timezone
        from datetime import timedelta
        # environments locked for more than 30 seconds - they should be unlocked
        BuildEnvironment.objects.filter(buildrequest__state__in=[BuildRequest.REQ_FAILED, BuildRequest.REQ_COMPLETED]).filter(lock=BuildEnvironment.LOCK_LOCK).filter(updated__lt = timezone.now() - timedelta(seconds = 30)).update(lock = BuildEnvironment.LOCK_FREE)


        # update all Builds that failed to start

        for br in BuildRequest.objects.filter(state = BuildRequest.REQ_FAILED, build__outcome = Build.IN_PROGRESS):
            # transpose the launch errors in ToasterExceptions
            br.build.outcome = Build.FAILED
            for brerror in br.brerror_set.all():
                logger.debug("Saving error %s" % brerror)
                LogMessage.objects.create(build = br.build, level = LogMessage.EXCEPTION, message = brerror.errmsg)
            br.build.save()

            # we don't have a true build object here; hence, toasterui didn't have a change to release the BE lock
            br.environment.lock = BuildEnvironment.LOCK_FREE
            br.environment.save()



        # update all BuildRequests without a build created
        for br in BuildRequest.objects.filter(build = None):
            br.build = Build.objects.create(project = br.project, completed_on = br.updated, started_on = br.created)
            br.build.outcome = Build.FAILED
            try:
                br.build.machine = br.brvariable_set.get(name='MACHINE').value
            except BRVariable.DoesNotExist:
                pass
            br.save()
            # transpose target information
            for brtarget in br.brtarget_set.all():
                Target.objects.create(build=br.build, target=brtarget.target, task=brtarget.task)
            # transpose the launch errors in ToasterExceptions
            for brerror in br.brerror_set.all():
                LogMessage.objects.create(build = br.build, level = LogMessage.EXCEPTION, message = brerror.errmsg)

            br.build.save()
        pass


    def handle_noargs(self, **options):
        while True:
            try:
                self.cleanup()
                self.archive()
                self.schedule()
            except:
                pass

            time.sleep(1)
