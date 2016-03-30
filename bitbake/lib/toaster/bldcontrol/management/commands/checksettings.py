from django.core.management.base import NoArgsCommand, CommandError
from django.db import transaction
from bldcontrol.bbcontroller import getBuildEnvironmentController, ShellCmdException
from bldcontrol.models import BuildRequest, BuildEnvironment, BRError
from orm.models import ToasterSetting, Build
import os
import traceback

def DN(path):
    if path is None:
        return ""
    else:
        return os.path.dirname(path)


class Command(NoArgsCommand):
    args = ""
    help = "Verifies that the configured settings are valid and usable, or prompts the user to fix the settings."

    def __init__(self, *args, **kwargs):
        super(Command, self).__init__(*args, **kwargs)
        self.guesspath = DN(DN(DN(DN(DN(DN(DN(__file__)))))))

    def _find_first_path_for_file(self, startdirectory, filename, level=0):
        if level < 0:
            return None
        dirs = []
        for i in os.listdir(startdirectory):
            j = os.path.join(startdirectory, i)
            if os.path.isfile(j):
                if i == filename:
                    return startdirectory
            elif os.path.isdir(j):
                dirs.append(j)
        for j in dirs:
            ret = self._find_first_path_for_file(j, filename, level - 1)
            if ret is not None:
                return ret
        return None

    def _recursive_list_directories(self, startdirectory, level=0):
        if level < 0:
            return []
        dirs = []
        try:
            for i in os.listdir(startdirectory):
                j = os.path.join(startdirectory, i)
                if os.path.isdir(j):
                    dirs.append(j)
        except OSError:
            pass
        for j in dirs:
            dirs = dirs + self._recursive_list_directories(j, level - 1)
        return dirs


    def _verify_build_environment(self):
        # provide a local build env. This will be extended later to include non local
        if BuildEnvironment.objects.count() == 0:
            BuildEnvironment.objects.create(betype=BuildEnvironment.TYPE_LOCAL)

        # we make sure we have builddir and sourcedir for all defined build envionments
        for be in BuildEnvironment.objects.all():
            be.needs_import = False
            def _verify_be():
                is_changed = False

                def _update_sourcedir():
                    be.sourcedir = os.environ.get('TOASTER_DIR')
                    return True

                if len(be.sourcedir) == 0:
                    print "\n -- Validation: The layers checkout directory must be set."
                    is_changed = _update_sourcedir()

                if not be.sourcedir.startswith("/"):
                    print "\n -- Validation: The layers checkout directory must be set to an absolute path."
                    is_changed = _update_sourcedir()

                if is_changed:
                    if be.betype == BuildEnvironment.TYPE_LOCAL:
                        be.needs_import = True
                    return True

                def _update_builddir():
                    be.builddir = os.environ.get('TOASTER_DIR')+"/build"
                    return True

                if len(be.builddir) == 0:
                    print "\n -- Validation: The build directory must be set."
                    is_changed = _update_builddir()

                if not be.builddir.startswith("/"):
                    print "\n -- Validation: The build directory must to be set to an absolute path."
                    is_changed = _update_builddir()


                if is_changed:
                    print "\nBuild configuration saved"
                    be.save()
                    return True


                if be.needs_import:
                    try:
                        config_file = os.environ.get('TOASTER_CONF')
                        print "\nImporting file: %s" % config_file
                        from loadconf import Command as LoadConfigCommand

                        LoadConfigCommand()._import_layer_config(config_file)
                        # we run lsupdates after config update
                        print "\nLayer configuration imported. Updating information from the layer sources, please wait.\nYou can re-update any time later by running bitbake/lib/toaster/manage.py lsupdates"
                        from django.core.management import call_command
                        call_command("lsupdates")

                        # we don't look for any other config files
                        return is_changed
                    except Exception as e:
                        print "Failure while trying to import the toaster config file %s: %s" %\
                            (config_file, e)
                        traceback.print_exc(e)

                return is_changed

            while _verify_be():
                pass
        return 0

    def _verify_default_settings(self):
        # verify that default settings are there
        if ToasterSetting.objects.filter(name='DEFAULT_RELEASE').count() != 1:
            ToasterSetting.objects.filter(name='DEFAULT_RELEASE').delete()
            ToasterSetting.objects.get_or_create(name='DEFAULT_RELEASE', value='')
        return 0

    def _verify_builds_in_progress(self):
        # we are just starting up. we must not have any builds in progress, or build environments taken
        for b in BuildRequest.objects.filter(state=BuildRequest.REQ_INPROGRESS):
            BRError.objects.create(req=b, errtype="toaster",
                                   errmsg=
                                   "Toaster found this build IN PROGRESS while Toaster started up. This is an inconsistent state, and the build was marked as failed")

        BuildRequest.objects.filter(state=BuildRequest.REQ_INPROGRESS).update(state=BuildRequest.REQ_FAILED)

        BuildEnvironment.objects.update(lock=BuildEnvironment.LOCK_FREE)

        # also mark "In Progress builds as failures"
        from django.utils import timezone
        Build.objects.filter(outcome=Build.IN_PROGRESS).update(outcome=Build.FAILED, completed_on=timezone.now())

        return 0



    def handle_noargs(self, **options):
        retval = 0
        retval += self._verify_build_environment()
        retval += self._verify_default_settings()
        retval += self._verify_builds_in_progress()

        return retval
