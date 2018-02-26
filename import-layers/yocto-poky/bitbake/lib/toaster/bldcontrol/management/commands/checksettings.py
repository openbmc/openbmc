from django.core.management.base import BaseCommand, CommandError
from django.db import transaction

from django.core.management import call_command
from bldcontrol.bbcontroller import getBuildEnvironmentController, ShellCmdException
from bldcontrol.models import BuildRequest, BuildEnvironment, BRError
from orm.models import ToasterSetting, Build, Layer

import os
import traceback
import warnings


def DN(path):
    if path is None:
        return ""
    else:
        return os.path.dirname(path)


class Command(BaseCommand):
    args = ""
    help = "Verifies that the configured settings are valid and usable, or prompts the user to fix the settings."

    def __init__(self, *args, **kwargs):
        super(Command, self).__init__(*args, **kwargs)
        self.guesspath = DN(DN(DN(DN(DN(DN(DN(__file__)))))))

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
                    is_changed = _update_sourcedir()

                if not be.sourcedir.startswith("/"):
                    print("\n -- Validation: The layers checkout directory must be set to an absolute path.")
                    is_changed = _update_sourcedir()

                if is_changed:
                    if be.betype == BuildEnvironment.TYPE_LOCAL:
                        be.needs_import = True
                    return True

                def _update_builddir():
                    be.builddir = os.environ.get('TOASTER_DIR')+"/build"
                    return True

                if len(be.builddir) == 0:
                    is_changed = _update_builddir()

                if not be.builddir.startswith("/"):
                    print("\n -- Validation: The build directory must to be set to an absolute path.")
                    is_changed = _update_builddir()

                if is_changed:
                    print("\nBuild configuration saved")
                    be.save()
                    return True

                if be.needs_import:
                    try:
                        print("Loading default settings")
                        call_command("loaddata", "settings")
                        template_conf = os.environ.get("TEMPLATECONF", "")

                        if ToasterSetting.objects.filter(name='CUSTOM_XML_ONLY').count() > 0:
                            # only use the custom settings
                            pass
                        elif "poky" in template_conf:
                            print("Loading poky configuration")
                            call_command("loaddata", "poky")
                        else:
                            print("Loading OE-Core configuration")
                            call_command("loaddata", "oe-core")
                            if template_conf:
                                oe_core_path = os.path.realpath(
                                    template_conf +
                                    "/../")
                            else:
                                print("TEMPLATECONF not found. You may have to"
                                      " manually configure layer paths")
                                oe_core_path = input("Please enter the path of"
                                                     " your openembedded-core "
                                                     "layer: ")
                            # Update the layer instances of openemebedded-core
                            for layer in Layer.objects.filter(
                                    name="openembedded-core",
                                    local_source_dir="OE-CORE-LAYER-DIR"):
                                layer.local_path = oe_core_path
                                layer.save()

                        # Import the custom fixture if it's present
                        with warnings.catch_warnings():
                            warnings.filterwarnings(
                                action="ignore",
                                message="^.*No fixture named.*$")
                            print("Importing custom settings if present")
                            call_command("loaddata", "custom")

                        # we run lsupdates after config update
                        print("\nFetching information from the layer index, "
                              "please wait.\nYou can re-update any time later "
                              "by running bitbake/lib/toaster/manage.py "
                              "lsupdates\n")
                        call_command("lsupdates")

                        # we don't look for any other config files
                        return is_changed
                    except Exception as e:
                        print("Failure while trying to setup toaster: %s"
                              % e)
                        traceback.print_exc()

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



    def handle(self, **options):
        retval = 0
        retval += self._verify_build_environment()
        retval += self._verify_default_settings()
        retval += self._verify_builds_in_progress()

        return retval
