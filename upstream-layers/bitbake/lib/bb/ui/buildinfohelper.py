#
# BitBake ToasterUI Implementation
#
# Copyright (C) 2013        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import sys
import bb
import re
import os

import django
from django.utils import timezone

import toaster
# Add toaster module to the search path to help django.setup() find the right
# modules
sys.path.insert(0, os.path.dirname(toaster.__file__))

#Set the DJANGO_SETTINGS_MODULE if it's not already set
os.environ["DJANGO_SETTINGS_MODULE"] =\
    os.environ.get("DJANGO_SETTINGS_MODULE",
                   "toaster.toastermain.settings")
# Setup django framework (needs to be done before importing modules)
django.setup()

from orm.models import Build, Task, Recipe, Layer_Version, Layer, Target, LogMessage, HelpText
from orm.models import Target_Image_File, TargetKernelFile, TargetSDKFile
from orm.models import Variable, VariableHistory
from orm.models import Package, Package_File, Target_Installed_Package, Target_File
from orm.models import Task_Dependency, Package_Dependency
from orm.models import Recipe_Dependency, Provides
from orm.models import Project, CustomImagePackage
from orm.models import signal_runbuilds

from bldcontrol.models import BuildEnvironment, BuildRequest
from bldcontrol.models import BRLayer
from bldcontrol import bbcontroller

from bb.msg import BBLogFormatter as formatter
from django.db import models
from pprint import pformat
import logging
from datetime import datetime, timedelta

from django.db import transaction


# pylint: disable=invalid-name
# the logger name is standard throughout BitBake
logger = logging.getLogger("ToasterLogger")

class NotExisting(Exception):
    pass

class ORMWrapper(object):
    """ This class creates the dictionaries needed to store information in the database
        following the format defined by the Django models. It is also used to save this
        information in the database.
    """

    def __init__(self):
        self.layer_version_objects = []
        self.layer_version_built = []
        self.task_objects = {}
        self.recipe_objects = {}

    @staticmethod
    def _build_key(**kwargs):
        key = "0"
        for k in sorted(kwargs.keys()):
            if isinstance(kwargs[k], models.Model):
                key += "-%d" % kwargs[k].id
            else:
                key += "-%s" % str(kwargs[k])
        return key


    def _cached_get_or_create(self, clazz, **kwargs):
        """ This is a memory-cached get_or_create. We assume that the objects will not be created in the
            database through any other means.
        """

        assert issubclass(clazz, models.Model), "_cached_get_or_create needs to get the class as first argument"

        key = ORMWrapper._build_key(**kwargs)
        dictname = "objects_%s" % clazz.__name__
        if not dictname in vars(self).keys():
            vars(self)[dictname] = {}

        created = False
        if not key in vars(self)[dictname].keys():
            vars(self)[dictname][key], created = \
                clazz.objects.get_or_create(**kwargs)

        return (vars(self)[dictname][key], created)


    def _cached_get(self, clazz, **kwargs):
        """ This is a memory-cached get. We assume that the objects will not change  in the database between gets.
        """
        assert issubclass(clazz, models.Model), "_cached_get needs to get the class as first argument"

        key = ORMWrapper._build_key(**kwargs)
        dictname = "objects_%s" % clazz.__name__

        if not dictname in vars(self).keys():
            vars(self)[dictname] = {}

        if not key in vars(self)[dictname].keys():
            vars(self)[dictname][key] = clazz.objects.get(**kwargs)

        return vars(self)[dictname][key]

    def get_similar_target_with_image_files(self, target):
        """
        Get a Target object "similar" to target; i.e. with the same target
        name ('core-image-minimal' etc.) and machine.
        """
        return target.get_similar_target_with_image_files()

    def get_similar_target_with_sdk_files(self, target):
        return target.get_similar_target_with_sdk_files()

    def clone_image_artifacts(self, target_from, target_to):
        target_to.clone_image_artifacts_from(target_from)

    def clone_sdk_artifacts(self, target_from, target_to):
        target_to.clone_sdk_artifacts_from(target_from)

    def _timestamp_to_datetime(self, secs):
        """
        Convert timestamp in seconds to Python datetime
        """
        return timezone.make_aware(datetime(1970, 1, 1) + timedelta(seconds=secs))

    # pylint: disable=no-self-use
    # we disable detection of no self use in functions because the methods actually work on the object
    # even if they don't touch self anywhere

    # pylint: disable=bad-continuation
    # we do not follow the python conventions for continuation indentation due to long lines here

    def get_or_create_build_object(self, brbe):
        prj = None
        buildrequest = None
        if brbe is not None:
            # Toaster-triggered build
            logger.debug("buildinfohelper: brbe is %s" % brbe)
            br, _ = brbe.split(":")
            buildrequest = BuildRequest.objects.get(pk=br)
            prj = buildrequest.project
        else:
            # CLI build
            prj = Project.objects.get_or_create_default_project()
            logger.debug("buildinfohelper: project is not specified, defaulting to %s" % prj)

        if buildrequest is not None:
            # reuse existing Build object
            build = buildrequest.build
            build.project = prj
            build.save()
        else:
            # create new Build object
            now = timezone.now()
            build = Build.objects.create(
                project=prj,
                started_on=now,
                completed_on=now,
                build_name='')

        logger.debug("buildinfohelper: build is created %s" % build)

        if buildrequest is not None:
            buildrequest.build = build
            buildrequest.save()

        return build

    def update_build(self, build, data_dict):
        for key in data_dict:
            setattr(build, key, data_dict[key])
        build.save()

    @staticmethod
    def get_or_create_targets(target_info):
        """
        NB get_or_create() is used here because for Toaster-triggered builds,
        we already created the targets when the build was triggered.
        """
        result = []
        for target in target_info['targets']:
            task = ''
            if ':' in target:
                target, task = target.split(':', 1)
            if task.startswith('do_'):
                task = task[3:]
            if task == 'build':
                task = ''

            obj, _ = Target.objects.get_or_create(build=target_info['build'],
                                                  target=target,
                                                  task=task)
            result.append(obj)
        return result

    def update_build_stats_and_outcome(self, build, errors, warnings, taskfailures):
        assert isinstance(build,Build)
        assert isinstance(errors, int)
        assert isinstance(warnings, int)

        if build.outcome == Build.CANCELLED:
            return
        try:
            if build.buildrequest.state == BuildRequest.REQ_CANCELLING:
                return
        except AttributeError:
            # We may not have a buildrequest if this is a command line build
            pass

        outcome = Build.SUCCEEDED
        if errors or taskfailures:
            outcome = Build.FAILED

        build.completed_on = timezone.now()
        build.outcome = outcome
        build.save()

        # We force a sync point here to force the outcome status commit,
        # which resolves a race condition with the build completion takedown
        transaction.set_autocommit(True)
        transaction.set_autocommit(False)

        signal_runbuilds()

    def update_target_set_license_manifest(self, target, license_manifest_path):
        target.license_manifest_path = license_manifest_path
        target.save()

    def update_target_set_package_manifest(self, target, package_manifest_path):
        target.package_manifest_path = package_manifest_path
        target.save()

    def update_task_object(self, build, task_name, recipe_name, task_stats):
        """
        Find the task for build which matches the recipe and task name
        to be stored
        """
        task_to_update = Task.objects.get(
            build = build,
            task_name = task_name,
            recipe__name = recipe_name
        )

        if 'started' in task_stats and 'ended' in task_stats:
            task_to_update.started = self._timestamp_to_datetime(task_stats['started'])
            task_to_update.ended = self._timestamp_to_datetime(task_stats['ended'])
            task_to_update.elapsed_time = (task_stats['ended'] - task_stats['started'])
        task_to_update.cpu_time_user = task_stats.get('cpu_time_user')
        task_to_update.cpu_time_system = task_stats.get('cpu_time_system')
        if 'disk_io_read' in task_stats and 'disk_io_write' in task_stats:
            task_to_update.disk_io_read = task_stats['disk_io_read']
            task_to_update.disk_io_write = task_stats['disk_io_write']
            task_to_update.disk_io = task_stats['disk_io_read'] + task_stats['disk_io_write']

        task_to_update.save()

    def get_update_task_object(self, task_information, must_exist = False):
        assert 'build' in task_information
        assert 'recipe' in task_information
        assert 'task_name' in task_information

        # we use must_exist info for database look-up optimization
        task_object, created = self._cached_get_or_create(Task,
                        build=task_information['build'],
                        recipe=task_information['recipe'],
                        task_name=task_information['task_name']
                        )
        if created and must_exist:
            task_information['debug'] = "build id %d, recipe id %d" % (task_information['build'].pk, task_information['recipe'].pk)
            raise NotExisting("Task object created when expected to exist", task_information)

        object_changed = False
        for v in vars(task_object):
            if v in task_information.keys():
                if vars(task_object)[v] != task_information[v]:
                    vars(task_object)[v] = task_information[v]
                    object_changed = True

        # update setscene-related information if the task has a setscene
        if task_object.outcome == Task.OUTCOME_COVERED and 1 == task_object.get_related_setscene().count():
            task_object.outcome = Task.OUTCOME_CACHED
            object_changed = True

            outcome_task_setscene = Task.objects.get(task_executed=True, build = task_object.build,
                                    recipe = task_object.recipe, task_name=task_object.task_name+"_setscene").outcome
            if outcome_task_setscene == Task.OUTCOME_SUCCESS:
                task_object.sstate_result = Task.SSTATE_RESTORED
                object_changed = True
            elif outcome_task_setscene == Task.OUTCOME_FAILED:
                task_object.sstate_result = Task.SSTATE_FAILED
                object_changed = True

        if object_changed:
            task_object.save()
        return task_object


    def get_update_recipe_object(self, recipe_information, must_exist = False):
        assert 'layer_version' in recipe_information
        assert 'file_path' in recipe_information
        assert 'pathflags' in recipe_information

        assert not recipe_information['file_path'].startswith("/")      # we should have layer-relative paths at all times


        def update_recipe_obj(recipe_object):
            object_changed = False
            for v in vars(recipe_object):
                if v in recipe_information.keys():
                    object_changed = True
                    vars(recipe_object)[v] = recipe_information[v]

            if object_changed:
                recipe_object.save()

        recipe, created = self._cached_get_or_create(Recipe, layer_version=recipe_information['layer_version'],
                                     file_path=recipe_information['file_path'], pathflags = recipe_information['pathflags'])

        update_recipe_obj(recipe)

        built_recipe = None
        # Create a copy of the recipe for historical puposes and update it
        for built_layer in self.layer_version_built:
            if built_layer.layer == recipe_information['layer_version'].layer:
                built_recipe, c = self._cached_get_or_create(Recipe,
                        layer_version=built_layer,
                        file_path=recipe_information['file_path'],
                        pathflags = recipe_information['pathflags'])
                update_recipe_obj(built_recipe)
                break


        # If we're in analysis mode or if this is a custom recipe
        # then we are wholly responsible for the data
        # and therefore we return the 'real' recipe rather than the build
        # history copy of the recipe.
        if  recipe_information['layer_version'].build is not None and \
            recipe_information['layer_version'].build.project == \
                Project.objects.get_or_create_default_project():
            return recipe

        if built_recipe is None:
            return recipe

        return built_recipe

    def get_update_layer_version_object(self, build_obj, layer_obj, layer_version_information):
        if isinstance(layer_obj, Layer_Version):
            # We already found our layer version for this build so just
            # update it with the new build information
            logger.debug("We found our layer from toaster")
            layer_obj.local_path = layer_version_information['local_path']
            layer_obj.save()
            self.layer_version_objects.append(layer_obj)

            # create a new copy of this layer version as a snapshot for
            # historical purposes
            layer_copy, c = Layer_Version.objects.get_or_create(
                build=build_obj,
                layer=layer_obj.layer,
                release=layer_obj.release,
                branch=layer_version_information['branch'],
                commit=layer_version_information['commit'],
                local_path=layer_version_information['local_path'],
            )

            logger.debug("Created new layer version %s for build history",
                         layer_copy.layer.name)

            self.layer_version_built.append(layer_copy)

            return layer_obj

        assert isinstance(build_obj, Build)
        assert isinstance(layer_obj, Layer)
        assert 'branch' in layer_version_information
        assert 'commit' in layer_version_information
        assert 'priority' in layer_version_information
        assert 'local_path' in layer_version_information

        # If we're doing a command line build then associate this new layer with the
        # project to avoid it 'contaminating' toaster data
        project = None
        if build_obj.project == Project.objects.get_or_create_default_project():
            project = build_obj.project

        layer_version_object, _ = Layer_Version.objects.get_or_create(
                                  build = build_obj,
                                  layer = layer_obj,
                                  branch = layer_version_information['branch'],
                                  commit = layer_version_information['commit'],
                                  priority = layer_version_information['priority'],
                                  local_path = layer_version_information['local_path'],
                                  project=project)

        self.layer_version_objects.append(layer_version_object)

        return layer_version_object

    def get_update_layer_object(self, layer_information, brbe):
        assert 'name' in layer_information
        assert 'layer_index_url' in layer_information

        # From command line builds we have no brbe as the request is directly
        # from bitbake
        if brbe is None:
            # If we don't have git commit sha then we're using a non-git
            # layer so set the layer_source_dir to identify it as such
            if not layer_information['version']['commit']:
                local_source_dir = layer_information["local_path"]
            else:
                local_source_dir = None

            layer_object, _ = \
                Layer.objects.get_or_create(
                    name=layer_information['name'],
                    local_source_dir=local_source_dir,
                    layer_index_url=layer_information['layer_index_url'])

            return layer_object
        else:
            br_id, be_id = brbe.split(":")

            # Find the layer version by matching the layer event information
            # against the metadata we have in Toaster

            try:
                br_layer = BRLayer.objects.get(req=br_id,
                                               name=layer_information['name'])
                return br_layer.layer_version
            except (BRLayer.MultipleObjectsReturned, BRLayer.DoesNotExist):
                # There are multiple of the same layer name or the name
                # hasn't been determined by the toaster.bbclass layer
                # so let's filter by the local_path
                bc = bbcontroller.getBuildEnvironmentController(pk=be_id)
                for br_layer in BRLayer.objects.filter(req=br_id):
                    if br_layer.giturl and \
                       layer_information['local_path'].endswith(
                           bc.getGitCloneDirectory(br_layer.giturl,
                                                   br_layer.commit)):
                            return br_layer.layer_version

                    if br_layer.local_source_dir == \
                            layer_information['local_path']:
                        return br_layer.layer_version

        # We've reached the end of our search and couldn't find the layer
        # we can continue but some data may be missing
        raise NotExisting("Unidentified layer %s" %
                          pformat(layer_information))

    def save_target_file_information(self, build_obj, target_obj, filedata):
        assert isinstance(build_obj, Build)
        assert isinstance(target_obj, Target)
        dirs = filedata['dirs']
        files = filedata['files']
        syms = filedata['syms']

        # always create the root directory as a special case;
        # note that this is never displayed, so the owner, group,
        # size, permission are irrelevant
        tf_obj = Target_File.objects.create(target = target_obj,
                                            path = '/',
                                            size = 0,
                                            owner = '',
                                            group = '',
                                            permission = '',
                                            inodetype = Target_File.ITYPE_DIRECTORY)
        tf_obj.save()

        # insert directories, ordered by name depth
        for d in sorted(dirs, key=lambda x:len(x[-1].split("/"))):
            (user, group, size) = d[1:4]
            permission = d[0][1:]
            path = d[4].lstrip(".")

            # we already created the root directory, so ignore any
            # entry for it
            if not path:
                continue

            parent_path = "/".join(path.split("/")[:len(path.split("/")) - 1])
            if not parent_path:
                parent_path = "/"
            parent_obj = self._cached_get(Target_File, target = target_obj, path = parent_path, inodetype = Target_File.ITYPE_DIRECTORY)
            Target_File.objects.create(
                        target = target_obj,
                        path = path,
                        size = size,
                        inodetype = Target_File.ITYPE_DIRECTORY,
                        permission = permission,
                        owner = user,
                        group = group,
                        directory = parent_obj)


        # we insert files
        for d in files:
            (user, group, size) = d[1:4]
            permission = d[0][1:]
            path = d[4].lstrip(".")
            parent_path = "/".join(path.split("/")[:len(path.split("/")) - 1])
            inodetype = Target_File.ITYPE_REGULAR
            if d[0].startswith('b'):
                inodetype = Target_File.ITYPE_BLOCK
            if d[0].startswith('c'):
                inodetype = Target_File.ITYPE_CHARACTER
            if d[0].startswith('p'):
                inodetype = Target_File.ITYPE_FIFO

            tf_obj = Target_File.objects.create(
                        target = target_obj,
                        path = path,
                        size = size,
                        inodetype = inodetype,
                        permission = permission,
                        owner = user,
                        group = group)
            parent_obj = self._cached_get(Target_File, target = target_obj, path = parent_path, inodetype = Target_File.ITYPE_DIRECTORY)
            tf_obj.directory = parent_obj
            tf_obj.save()

        # we insert symlinks
        for d in syms:
            (user, group, size) = d[1:4]
            permission = d[0][1:]
            path = d[4].lstrip(".")
            filetarget_path = d[6]

            parent_path = "/".join(path.split("/")[:len(path.split("/")) - 1])
            if not filetarget_path.startswith("/"):
                # we have a relative path, get a normalized absolute one
                filetarget_path = parent_path + "/" + filetarget_path
                fcp = filetarget_path.split("/")
                fcpl = []
                for i in fcp:
                    if i == "..":
                        fcpl.pop()
                    else:
                        fcpl.append(i)
                filetarget_path = "/".join(fcpl)

            try:
                filetarget_obj = Target_File.objects.get(target = target_obj, path = filetarget_path)
            except Target_File.DoesNotExist:
                # we might have an invalid link; no way to detect this. just set it to None
                filetarget_obj = None

            try:
                parent_obj = Target_File.objects.get(target = target_obj, path = parent_path, inodetype = Target_File.ITYPE_DIRECTORY)
            except Target_File.DoesNotExist:
                parent_obj = None

            Target_File.objects.create(
                        target = target_obj,
                        path = path,
                        size = size,
                        inodetype = Target_File.ITYPE_SYMLINK,
                        permission = permission,
                        owner = user,
                        group = group,
                        directory = parent_obj,
                        sym_target = filetarget_obj)


    def save_target_package_information(self, build_obj, target_obj, packagedict, pkgpnmap, recipes, built_package=False):
        assert isinstance(build_obj, Build)
        assert isinstance(target_obj, Target)

        errormsg = []
        for p in packagedict:
            # Search name swtiches round the installed name vs package name
            # by default installed name == package name
            searchname = p
            if p not in pkgpnmap:
                logger.warning("Image packages list contains %p, but is"
                               " missing from all packages list where the"
                               " metadata comes from. Skipping...", p)
                continue

            if 'OPKGN' in pkgpnmap[p].keys():
                searchname = pkgpnmap[p]['OPKGN']

            built_recipe = recipes[pkgpnmap[p]['PN']]

            if built_package:
                packagedict[p]['object'], created = Package.objects.get_or_create( build = build_obj, name = searchname )
                recipe = built_recipe
            else:
                packagedict[p]['object'], created = \
                        CustomImagePackage.objects.get_or_create(name=searchname)
                # Clear the Package_Dependency objects as we're going to update
                # the CustomImagePackage with the latest dependency information
                packagedict[p]['object'].package_dependencies_target.all().delete()
                packagedict[p]['object'].package_dependencies_source.all().delete()
                try:
                    recipe = self._cached_get(
                        Recipe,
                        name=built_recipe.name,
                        layer_version__build=None,
                        layer_version__release=
                        built_recipe.layer_version.release,
                        file_path=built_recipe.file_path,
                        version=built_recipe.version
                    )
                except (Recipe.DoesNotExist,
                        Recipe.MultipleObjectsReturned) as e:
                    logger.info("We did not find one recipe for the"
                                "configuration data package %s %s" % (p, e))
                    continue

            if created or packagedict[p]['object'].size == -1:    # save the data anyway we can, not just if it was not created here; bug [YOCTO #6887]
                # fill in everything we can from the runtime-reverse package data
                try:
                    packagedict[p]['object'].recipe = recipe
                    packagedict[p]['object'].version = pkgpnmap[p]['PV']
                    packagedict[p]['object'].installed_name = p
                    packagedict[p]['object'].revision = pkgpnmap[p]['PR']
                    packagedict[p]['object'].license = pkgpnmap[p]['LICENSE']
                    packagedict[p]['object'].section = pkgpnmap[p]['SECTION']
                    packagedict[p]['object'].summary = pkgpnmap[p]['SUMMARY']
                    packagedict[p]['object'].description = pkgpnmap[p]['DESCRIPTION']
                    packagedict[p]['object'].size = int(pkgpnmap[p]['PKGSIZE'])

                # no files recorded for this package, so save files info
                    packagefile_objects = []
                    for targetpath in pkgpnmap[p]['FILES_INFO']:
                        targetfilesize = pkgpnmap[p]['FILES_INFO'][targetpath]
                        packagefile_objects.append(Package_File( package = packagedict[p]['object'],
                            path = targetpath,
                            size = targetfilesize))
                    if packagefile_objects:
                        Package_File.objects.bulk_create(packagefile_objects)
                except KeyError as e:
                    errormsg.append("  stpi: Key error, package %s key %s \n" % (p, e))

            # save disk installed size
            packagedict[p]['object'].installed_size = packagedict[p]['size']
            packagedict[p]['object'].save()

            if built_package:
                Target_Installed_Package.objects.create(target = target_obj, package = packagedict[p]['object'])

        packagedeps_objs = []
        pattern_so = re.compile(r'.*\.so(\.\d*)?$')
        pattern_lib = re.compile(r'.*\-suffix(\d*)?$')
        pattern_ko = re.compile(r'^kernel-module-.*')
        for p in packagedict:
            for (px,deptype) in packagedict[p]['depends']:
                if deptype == 'depends':
                    tdeptype = Package_Dependency.TYPE_TRDEPENDS
                elif deptype == 'recommends':
                    tdeptype = Package_Dependency.TYPE_TRECOMMENDS

                try:
                    # Skip known non-package objects like libraries and kernel modules
                    if pattern_so.match(px) or pattern_lib.match(px):
                        logger.info("Toaster does not add library file dependencies to packages (%s,%s)", p, px)
                        continue
                    if pattern_ko.match(px):
                        logger.info("Toaster does not add kernel module dependencies to packages (%s,%s)", p, px)
                        continue
                    packagedeps_objs.append(Package_Dependency(
                        package = packagedict[p]['object'],
                        depends_on = packagedict[px]['object'],
                        dep_type = tdeptype,
                        target = target_obj))
                except KeyError as e:
                    logger.warning("Could not add dependency to the package %s "
                                   "because %s is an unknown package", p, px)

        if packagedeps_objs:
            Package_Dependency.objects.bulk_create(packagedeps_objs)
        else:
            logger.info("No package dependencies created")

        if errormsg:
            logger.warning("buildinfohelper: target_package_info could not identify recipes: \n%s", "".join(errormsg))

    def save_target_image_file_information(self, target_obj, file_name, file_size):
        Target_Image_File.objects.create(target=target_obj,
            file_name=file_name, file_size=file_size)

    def save_target_kernel_file(self, target_obj, file_name, file_size):
        """
        Save kernel file (bzImage, modules*) information for a Target target_obj.
        """
        TargetKernelFile.objects.create(target=target_obj,
            file_name=file_name, file_size=file_size)

    def save_target_sdk_file(self, target_obj, file_name, file_size):
        """
        Save SDK artifacts to the database, associating them with a
        Target object.
        """
        TargetSDKFile.objects.create(target=target_obj, file_name=file_name,
            file_size=file_size)

    def create_logmessage(self, log_information):
        assert 'build' in log_information
        assert 'level' in log_information
        assert 'message' in log_information

        log_object = LogMessage.objects.create(
                        build = log_information['build'],
                        level = log_information['level'],
                        message = log_information['message'])

        for v in vars(log_object):
            if v in log_information.keys():
                vars(log_object)[v] = log_information[v]

        return log_object.save()


    def save_build_package_information(self, build_obj, package_info, recipes,
                                       built_package):
        # assert isinstance(build_obj, Build)

        if not 'PN' in package_info.keys():
            # no package data to save (e.g. 'OPKGN'="lib64-*"|"lib32-*")
            return None

        # create and save the object
        pname = package_info['PKG']
        built_recipe = recipes[package_info['PN']]
        if 'OPKGN' in package_info.keys():
            pname = package_info['OPKGN']

        if built_package:
            bp_object, _ = Package.objects.get_or_create( build = build_obj,
                                                         name = pname )
            recipe = built_recipe
        else:
            bp_object, created = \
                    CustomImagePackage.objects.get_or_create(name=pname)
            try:
                recipe = self._cached_get(Recipe,
                                          name=built_recipe.name,
                                          layer_version__build=None,
                                          file_path=built_recipe.file_path,
                                          version=built_recipe.version)

            except (Recipe.DoesNotExist, Recipe.MultipleObjectsReturned):
                logger.debug("We did not find one recipe for the configuration"
                             "data package %s" % pname)
                return

        bp_object.installed_name = package_info['PKG']
        bp_object.recipe = recipe
        bp_object.version = package_info['PKGV']
        bp_object.revision = package_info['PKGR']
        bp_object.summary = package_info['SUMMARY']
        bp_object.description = package_info['DESCRIPTION']
        bp_object.size = int(package_info['PKGSIZE'])
        bp_object.section = package_info['SECTION']
        bp_object.license = package_info['LICENSE']
        bp_object.save()

        # save any attached file information
        packagefile_objects = []
        for path in package_info['FILES_INFO']:
            packagefile_objects.append(Package_File( package = bp_object,
                                        path = path,
                                        size = package_info['FILES_INFO'][path] ))
        if packagefile_objects:
            Package_File.objects.bulk_create(packagefile_objects)

        def _po_byname(p):
            if built_package:
                pkg, created = Package.objects.get_or_create(build=build_obj,
                                                             name=p)
            else:
                pkg, created = CustomImagePackage.objects.get_or_create(name=p)

            if created:
                pkg.size = -1
                pkg.save()
            return pkg

        packagedeps_objs = []
        # save soft dependency information
        if 'RDEPENDS' in package_info and package_info['RDEPENDS']:
            for p in bb.utils.explode_deps(package_info['RDEPENDS']):
                packagedeps_objs.append(Package_Dependency(  package = bp_object,
                    depends_on = _po_byname(p), dep_type = Package_Dependency.TYPE_RDEPENDS))
        if 'RPROVIDES' in package_info and package_info['RPROVIDES']:
            for p in bb.utils.explode_deps(package_info['RPROVIDES']):
                packagedeps_objs.append(Package_Dependency(  package = bp_object,
                    depends_on = _po_byname(p), dep_type = Package_Dependency.TYPE_RPROVIDES))
        if 'RRECOMMENDS' in package_info and package_info['RRECOMMENDS']:
            for p in bb.utils.explode_deps(package_info['RRECOMMENDS']):
                packagedeps_objs.append(Package_Dependency(  package = bp_object,
                    depends_on = _po_byname(p), dep_type = Package_Dependency.TYPE_RRECOMMENDS))
        if 'RSUGGESTS' in package_info and package_info['RSUGGESTS']:
            for p in bb.utils.explode_deps(package_info['RSUGGESTS']):
                packagedeps_objs.append(Package_Dependency(  package = bp_object,
                    depends_on = _po_byname(p), dep_type = Package_Dependency.TYPE_RSUGGESTS))
        if 'RREPLACES' in package_info and package_info['RREPLACES']:
            for p in bb.utils.explode_deps(package_info['RREPLACES']):
                packagedeps_objs.append(Package_Dependency(  package = bp_object,
                    depends_on = _po_byname(p), dep_type = Package_Dependency.TYPE_RREPLACES))
        if 'RCONFLICTS' in package_info and package_info['RCONFLICTS']:
            for p in bb.utils.explode_deps(package_info['RCONFLICTS']):
                packagedeps_objs.append(Package_Dependency(  package = bp_object,
                    depends_on = _po_byname(p), dep_type = Package_Dependency.TYPE_RCONFLICTS))

        if packagedeps_objs:
            Package_Dependency.objects.bulk_create(packagedeps_objs)

        return bp_object

    def save_build_variables(self, build_obj, vardump):
        assert isinstance(build_obj, Build)

        for k in vardump:
            desc = vardump[k]['doc']
            if desc is None:
                var_words = [word for word in k.split('_')]
                root_var = "_".join([word for word in var_words if word.isupper()])
                if root_var and root_var != k and root_var in vardump:
                    desc = vardump[root_var]['doc']
            if desc is None:
                desc = ''
            if desc:
                HelpText.objects.get_or_create(build=build_obj,
                                               area=HelpText.VARIABLE,
                                               key=k, text=desc)
            if not bool(vardump[k]['func']):
                value = vardump[k]['v']
                if value is None:
                    value = ''
                variable_obj = Variable.objects.create( build = build_obj,
                    variable_name = k,
                    variable_value = value,
                    description = desc)

                varhist_objects = []
                for vh in vardump[k]['history']:
                    if not 'documentation.conf' in vh['file']:
                        varhist_objects.append(VariableHistory( variable = variable_obj,
                                file_name = vh['file'],
                                line_number = vh['line'],
                                operation = vh['op']))
                if varhist_objects:
                    VariableHistory.objects.bulk_create(varhist_objects)


class MockEvent(object):
    """ This object is used to create event, for which normal event-processing methods can
        be used, out of data that is not coming via an actual event
    """
    def __init__(self):
        self.msg = None
        self.levelno = None
        self.taskname = None
        self.taskhash = None
        self.pathname = None
        self.lineno = None

    def getMessage(self):
        """
        Simulate LogRecord message return
        """
        return self.msg


class BuildInfoHelper(object):
    """ This class gathers the build information from the server and sends it
        towards the ORM wrapper for storing in the database
        It is instantiated once per build
        Keeps in memory all data that needs matching before writing it to the database
    """

    # tasks which produce image files; note we include '', as we set
    # the task for a target to '' (i.e. 'build') if no target is
    # explicitly defined
    IMAGE_GENERATING_TASKS = ['', 'build', 'image', 'populate_sdk_ext']

    # pylint: disable=protected-access
    # the code will look into the protected variables of the event; no easy way around this
    # pylint: disable=bad-continuation
    # we do not follow the python conventions for continuation indentation due to long lines here

    def __init__(self, server, has_build_history = False, brbe = None):
        self.internal_state = {}
        self.internal_state['taskdata'] = {}
        self.internal_state['targets'] = []
        self.task_order = 0
        self.autocommit_step = 1
        self.server = server
        self.orm_wrapper = ORMWrapper()
        self.has_build_history = has_build_history
        self.tmp_dir = self.server.runCommand(["getVariable", "TMPDIR"])[0]

        # this is set for Toaster-triggered builds by localhostbecontroller
        # via toasterui
        self.brbe = brbe

        self.project = None

        logger.debug("buildinfohelper: Build info helper inited %s" % vars(self))


    ###################
    ## methods to convert event/external info into objects that the ORM layer uses

    def _ensure_build(self):
        """
        Ensure the current build object exists and is up to date with
        data on the bitbake server
        """
        if not 'build' in self.internal_state or not self.internal_state['build']:
            # create the Build object
            self.internal_state['build'] = \
                self.orm_wrapper.get_or_create_build_object(self.brbe)

        build = self.internal_state['build']

        # update missing fields on the Build object with found data
        build_info = {}

        # set to True if at least one field is going to be set
        changed = False

        if not build.build_name:
            build_name = self.server.runCommand(["getVariable", "BUILDNAME"])[0]

            # only reset the build name if the one on the server is actually
            # a valid value for the build_name field
            if build_name is not None:
                build_info['build_name'] = build_name
                changed = True

        if not build.machine:
            build_info['machine'] = self.server.runCommand(["getVariable", "MACHINE"])[0]
            changed = True

        if not build.distro:
            build_info['distro'] = self.server.runCommand(["getVariable", "DISTRO"])[0]
            changed = True

        if not build.distro_version:
            build_info['distro_version'] = self.server.runCommand(["getVariable", "DISTRO_VERSION"])[0]
            changed = True

        if not build.bitbake_version:
            build_info['bitbake_version'] = self.server.runCommand(["getVariable", "BB_VERSION"])[0]
            changed = True

        if changed:
            self.orm_wrapper.update_build(self.internal_state['build'], build_info)

    def _get_task_information(self, event, recipe):
        assert 'taskname' in vars(event)
        self._ensure_build()

        task_information = {}
        task_information['build'] = self.internal_state['build']
        task_information['outcome'] = Task.OUTCOME_NA
        task_information['recipe'] = recipe
        task_information['task_name'] = event.taskname
        try:
            # some tasks don't come with a hash. and that's ok
            task_information['sstate_checksum'] = event.taskhash
        except AttributeError:
            pass
        return task_information

    def _get_layer_version_for_dependency(self, pathRE):
        """ Returns the layer in the toaster db that has a full regex
        match to the pathRE. pathRE - the layer path passed as a regex in the
        event. It is created in cooker.py as a collection for the layer
        priorities.
        """
        self._ensure_build()

        def _sort_longest_path(layer_version):
            assert isinstance(layer_version, Layer_Version)
            return len(layer_version.local_path)

        # Our paths don't append a trailing slash
        if pathRE.endswith("/"):
            pathRE = pathRE[:-1]

        p = re.compile(pathRE)
        path=re.sub(r'[$^]',r'',pathRE)
        # Heuristics: we always match recipe to the deepest layer path in
        # the discovered layers
        for lvo in sorted(self.orm_wrapper.layer_version_objects,
                          reverse=True, key=_sort_longest_path):
            if p.fullmatch(os.path.abspath(lvo.local_path)):
                return lvo
            if lvo.layer.local_source_dir:
                if p.fullmatch(os.path.abspath(lvo.layer.local_source_dir)):
                    return lvo
            if 0 == path.find(lvo.local_path):
                # sub-layer path inside existing layer
                return lvo

        # if we get here, we didn't read layers correctly;
        # dump whatever information we have on the error log
        logger.warning("Could not match layer dependency for path %s : %s",
                       pathRE,
                       self.orm_wrapper.layer_version_objects)
        return None

    def _get_layer_version_for_path(self, path):
        self._ensure_build()

        def _slkey_interactive(layer_version):
            assert isinstance(layer_version, Layer_Version)
            return len(layer_version.local_path)

        # Heuristics: we always match recipe to the deepest layer path in the discovered layers
        for lvo in sorted(self.orm_wrapper.layer_version_objects, reverse=True, key=_slkey_interactive):
            # we can match to the recipe file path
            if path.startswith(lvo.local_path):
                return lvo
            if lvo.layer.local_source_dir and \
               path.startswith(lvo.layer.local_source_dir):
                return lvo

        #if we get here, we didn't read layers correctly; dump whatever information we have on the error log
        logger.warning("Could not match layer version for recipe path %s : %s", path, self.orm_wrapper.layer_version_objects)

        #mockup the new layer
        unknown_layer, _ = Layer.objects.get_or_create(name="Unidentified layer", layer_index_url="")
        unknown_layer_version_obj, _ = Layer_Version.objects.get_or_create(layer = unknown_layer, build = self.internal_state['build'])

        # append it so we don't run into this error again and again
        self.orm_wrapper.layer_version_objects.append(unknown_layer_version_obj)

        return unknown_layer_version_obj

    def _get_recipe_information_from_taskfile(self, taskfile):
        localfilepath = taskfile.split(":")[-1]
        filepath_flags = ":".join(sorted(taskfile.split(":")[:-1]))
        layer_version_obj = self._get_layer_version_for_path(localfilepath)



        recipe_info = {}
        recipe_info['layer_version'] = layer_version_obj
        recipe_info['file_path'] = localfilepath
        recipe_info['pathflags'] = filepath_flags

        if recipe_info['file_path'].startswith(recipe_info['layer_version'].local_path):
            recipe_info['file_path'] = recipe_info['file_path'][len(recipe_info['layer_version'].local_path):].lstrip("/")
        else:
            raise RuntimeError("Recipe file path %s is not under layer version at %s" % (recipe_info['file_path'], recipe_info['layer_version'].local_path))

        return recipe_info


    ################################
    ## external available methods to store information
    @staticmethod
    def _get_data_from_event(event):
        evdata = None
        if '_localdata' in vars(event):
            evdata = event._localdata
        elif 'data' in vars(event):
            evdata = event.data
        else:
            raise Exception("Event with neither _localdata or data properties")
        return evdata

    def store_layer_info(self, event):
        layerinfos = BuildInfoHelper._get_data_from_event(event)
        self.internal_state['lvs'] = {}
        for layer in layerinfos:
            try:
                self.internal_state['lvs'][self.orm_wrapper.get_update_layer_object(layerinfos[layer], self.brbe)] = layerinfos[layer]['version']
                self.internal_state['lvs'][self.orm_wrapper.get_update_layer_object(layerinfos[layer], self.brbe)]['local_path'] = layerinfos[layer]['local_path']
            except NotExisting as nee:
                logger.warning("buildinfohelper: cannot identify layer exception:%s ", nee)

    def store_started_build(self):
        self._ensure_build()

    def save_build_log_file_path(self, build_log_path):
        self._ensure_build()

        if not self.internal_state['build'].cooker_log_path:
            data_dict = {'cooker_log_path': build_log_path}
            self.orm_wrapper.update_build(self.internal_state['build'], data_dict)

    def save_build_targets(self, event):
        self._ensure_build()

        # create target information
        assert '_pkgs' in vars(event)
        target_information = {}
        target_information['targets'] = event._pkgs
        target_information['build'] = self.internal_state['build']

        self.internal_state['targets'] = self.orm_wrapper.get_or_create_targets(target_information)

    def save_build_layers_and_variables(self):
        self._ensure_build()

        build_obj = self.internal_state['build']

        # save layer version information for this build
        if not 'lvs' in self.internal_state:
            logger.error("Layer version information not found; Check if the bitbake server was configured to inherit toaster.bbclass.")
        else:
            for layer_obj in self.internal_state['lvs']:
                self.orm_wrapper.get_update_layer_version_object(build_obj, layer_obj, self.internal_state['lvs'][layer_obj])

            del self.internal_state['lvs']

        # Save build configuration
        data = self.server.runCommand(["getAllKeysWithFlags", ["doc", "func"]])[0]

        # convert the paths from absolute to relative to either the build directory or layer checkouts
        path_prefixes = []

        if self.brbe is not None:
            _, be_id = self.brbe.split(":")
            be = BuildEnvironment.objects.get(pk = be_id)
            path_prefixes.append(be.builddir)

        for layer in sorted(self.orm_wrapper.layer_version_objects, key = lambda x:len(x.local_path), reverse=True):
            path_prefixes.append(layer.local_path)

        # we strip the prefixes
        for k in data:
            if not bool(data[k]['func']):
                for vh in data[k]['history']:
                    if not 'documentation.conf' in vh['file']:
                        abs_file_name = vh['file']
                        for pp in path_prefixes:
                            if abs_file_name.startswith(pp + "/"):
                                # preserve layer name in relative path
                                vh['file']=abs_file_name[pp.rfind("/")+1:]
                                break

        # save the variables
        self.orm_wrapper.save_build_variables(build_obj, data)

        return self.brbe

    def set_recipes_to_parse(self, num_recipes):
        """
        Set the number of recipes which need to be parsed for this build.
        This is set the first time ParseStarted is received by toasterui.
        """
        self._ensure_build()
        self.internal_state['build'].recipes_to_parse = num_recipes
        self.internal_state['build'].save()

    def set_recipes_parsed(self, num_recipes):
        """
        Set the number of recipes parsed so far for this build; this is updated
        each time a ParseProgress or ParseCompleted event is received by
        toasterui.
        """
        self._ensure_build()
        if num_recipes <= self.internal_state['build'].recipes_to_parse:
            self.internal_state['build'].recipes_parsed = num_recipes
            self.internal_state['build'].save()

    def update_target_image_file(self, event):
        evdata = BuildInfoHelper._get_data_from_event(event)

        for t in self.internal_state['targets']:
            if t.is_image:
                output_files = list(evdata.keys())
                for output in output_files:
                    if t.target in output and 'rootfs' in output and not output.endswith(".manifest"):
                        self.orm_wrapper.save_target_image_file_information(t, output, evdata[output])

    def update_artifact_image_file(self, event):
        self._ensure_build()
        evdata = BuildInfoHelper._get_data_from_event(event)
        for artifact_path in evdata.keys():
            self.orm_wrapper.save_artifact_information(
                self.internal_state['build'], artifact_path,
                evdata[artifact_path])

    def update_build_information(self, event, errors, warnings, taskfailures):
        self._ensure_build()
        self.orm_wrapper.update_build_stats_and_outcome(
            self.internal_state['build'], errors, warnings, taskfailures)

    def store_started_task(self, event):
        assert isinstance(event, (bb.runqueue.sceneQueueTaskStarted, bb.runqueue.runQueueTaskStarted, bb.runqueue.runQueueTaskSkipped))
        assert 'taskfile' in vars(event)
        localfilepath = event.taskfile.split(":")[-1]
        assert localfilepath.startswith("/")

        identifier = event.taskfile + ":" + event.taskname

        recipe_information = self._get_recipe_information_from_taskfile(event.taskfile)
        recipe = self.orm_wrapper.get_update_recipe_object(recipe_information, True)

        task_information = self._get_task_information(event, recipe)
        task_information['outcome'] = Task.OUTCOME_NA

        if isinstance(event, bb.runqueue.runQueueTaskSkipped):
            assert 'reason' in vars(event)
            task_information['task_executed'] = False
            if event.reason == "covered":
                task_information['outcome'] = Task.OUTCOME_COVERED
            if event.reason == "existing":
                task_information['outcome'] = Task.OUTCOME_PREBUILT
        else:
            task_information['task_executed'] = True
            if 'noexec' in vars(event) and event.noexec:
                task_information['task_executed'] = False
                task_information['outcome'] = Task.OUTCOME_EMPTY
                task_information['script_type'] = Task.CODING_NA

        # do not assign order numbers to scene tasks
        if not isinstance(event, bb.runqueue.sceneQueueTaskStarted):
            self.task_order += 1
            task_information['order'] = self.task_order

        self.orm_wrapper.get_update_task_object(task_information)

        self.internal_state['taskdata'][identifier] = {
                        'outcome': task_information['outcome'],
                    }


    def store_tasks_stats(self, event):
        self._ensure_build()
        task_data = BuildInfoHelper._get_data_from_event(event)

        for (task_file, task_name, task_stats, recipe_name) in task_data:
            build = self.internal_state['build']
            self.orm_wrapper.update_task_object(build, task_name, recipe_name, task_stats)

    def update_and_store_task(self, event):
        assert 'taskfile' in vars(event)
        localfilepath = event.taskfile.split(":")[-1]
        assert localfilepath.startswith("/")

        identifier = event.taskfile + ":" + event.taskname
        if not identifier in self.internal_state['taskdata']:
            if isinstance(event, bb.build.TaskBase):
                # we do a bit of guessing
                candidates = [x for x in self.internal_state['taskdata'].keys() if x.endswith(identifier)]
                if len(candidates) == 1:
                    identifier = candidates[0]
                elif len(candidates) > 1 and hasattr(event,'_package'):
                    if 'native-' in event._package:
                        identifier = 'native:' + identifier
                    if 'nativesdk-' in event._package:
                        identifier = 'nativesdk:' + identifier
                    candidates = [x for x in self.internal_state['taskdata'].keys() if x.endswith(identifier)]
                    if len(candidates) == 1:
                        identifier = candidates[0]

        assert identifier in self.internal_state['taskdata']
        identifierlist = identifier.split(":")
        realtaskfile = ":".join(identifierlist[0:len(identifierlist)-1])
        recipe_information = self._get_recipe_information_from_taskfile(realtaskfile)
        recipe = self.orm_wrapper.get_update_recipe_object(recipe_information, True)
        task_information = self._get_task_information(event,recipe)

        task_information['outcome'] = self.internal_state['taskdata'][identifier]['outcome']

        if 'logfile' in vars(event):
            task_information['logfile'] = event.logfile

        if '_message' in vars(event):
            task_information['message'] = event._message

        if 'taskflags' in vars(event):
            # with TaskStarted, we get even more information
            if 'python' in event.taskflags.keys() and event.taskflags['python'] == '1':
                task_information['script_type'] = Task.CODING_PYTHON
            else:
                task_information['script_type'] = Task.CODING_SHELL

        if task_information['outcome'] == Task.OUTCOME_NA:
            if isinstance(event, (bb.runqueue.runQueueTaskCompleted, bb.runqueue.sceneQueueTaskCompleted)):
                task_information['outcome'] = Task.OUTCOME_SUCCESS
                del self.internal_state['taskdata'][identifier]

            if isinstance(event, (bb.runqueue.runQueueTaskFailed, bb.runqueue.sceneQueueTaskFailed)):
                task_information['outcome'] = Task.OUTCOME_FAILED
                del self.internal_state['taskdata'][identifier]

        # we force a sync point here, to get the progress bar to show
        if self.autocommit_step % 3 == 0:
            transaction.set_autocommit(True)
            transaction.set_autocommit(False)
        self.autocommit_step += 1

        self.orm_wrapper.get_update_task_object(task_information, True) # must exist


    def store_missed_state_tasks(self, event):
        for (fn, taskname, taskhash, sstatefile) in BuildInfoHelper._get_data_from_event(event)['missed']:

            # identifier = fn + taskname + "_setscene"
            recipe_information = self._get_recipe_information_from_taskfile(fn)
            recipe = self.orm_wrapper.get_update_recipe_object(recipe_information)
            mevent = MockEvent()
            mevent.taskname = taskname
            mevent.taskhash = taskhash
            task_information = self._get_task_information(mevent,recipe)

            task_information['start_time'] = timezone.now()
            task_information['outcome'] = Task.OUTCOME_NA
            task_information['sstate_checksum'] = taskhash
            task_information['sstate_result'] = Task.SSTATE_MISS
            task_information['path_to_sstate_obj'] = sstatefile

            self.orm_wrapper.get_update_task_object(task_information)

        for (fn, taskname, taskhash, sstatefile) in BuildInfoHelper._get_data_from_event(event)['found']:

            # identifier = fn + taskname + "_setscene"
            recipe_information = self._get_recipe_information_from_taskfile(fn)
            recipe = self.orm_wrapper.get_update_recipe_object(recipe_information)
            mevent = MockEvent()
            mevent.taskname = taskname
            mevent.taskhash = taskhash
            task_information = self._get_task_information(mevent,recipe)

            task_information['path_to_sstate_obj'] = sstatefile

            self.orm_wrapper.get_update_task_object(task_information)


    def store_target_package_data(self, event):
        self._ensure_build()

        # for all image targets
        for target in self.internal_state['targets']:
            if target.is_image:
                pkgdata = BuildInfoHelper._get_data_from_event(event)['pkgdata']
                imgdata = BuildInfoHelper._get_data_from_event(event)['imgdata'].get(target.target, {})
                filedata = BuildInfoHelper._get_data_from_event(event)['filedata'].get(target.target, {})

                try:
                    self.orm_wrapper.save_target_package_information(self.internal_state['build'], target, imgdata, pkgdata, self.internal_state['recipes'], built_package=True)
                    self.orm_wrapper.save_target_package_information(self.internal_state['build'], target, imgdata.copy(), pkgdata, self.internal_state['recipes'], built_package=False)
                except KeyError as e:
                    logger.warning("KeyError in save_target_package_information"
                                   "%s ", e)

                # only try to find files in the image if the task for this
                # target is one which produces image files; otherwise, the old
                # list of files in the files-in-image.txt file will be
                # appended to the target even if it didn't produce any images
                if target.task in BuildInfoHelper.IMAGE_GENERATING_TASKS:
                    try:
                        self.orm_wrapper.save_target_file_information(self.internal_state['build'], target, filedata)
                    except KeyError as e:
                        logger.warning("KeyError in save_target_file_information"
                                       "%s ", e)



    def cancel_cli_build(self):
        """
        If a build is currently underway, set its state to CANCELLED;
        note that this only gets called for command line builds which are
        interrupted, so it doesn't touch any BuildRequest objects
        """
        self._ensure_build()
        self.internal_state['build'].outcome = Build.CANCELLED
        self.internal_state['build'].save()
        signal_runbuilds()

    def store_dependency_information(self, event):
        assert '_depgraph' in vars(event)
        assert 'layer-priorities' in event._depgraph
        assert 'pn' in event._depgraph
        assert 'tdepends' in event._depgraph

        errormsg = []

        # save layer version priorities
        if 'layer-priorities' in event._depgraph.keys():
            for lv in event._depgraph['layer-priorities']:
                (_, path, _, priority) = lv
                layer_version_obj = self._get_layer_version_for_dependency(path)
                if layer_version_obj:
                    layer_version_obj.priority = priority
                    layer_version_obj.save()

        # save recipe information
        self.internal_state['recipes'] = {}
        for pn in event._depgraph['pn']:

            file_name = event._depgraph['pn'][pn]['filename'].split(":")[-1]
            pathflags = ":".join(sorted(event._depgraph['pn'][pn]['filename'].split(":")[:-1]))
            layer_version_obj = self._get_layer_version_for_path(file_name)

            assert layer_version_obj is not None

            recipe_info = {}
            recipe_info['name'] = pn
            recipe_info['layer_version'] = layer_version_obj

            if 'version' in event._depgraph['pn'][pn]:
                recipe_info['version'] = event._depgraph['pn'][pn]['version'].lstrip(":")

            if 'summary' in event._depgraph['pn'][pn]:
                recipe_info['summary'] = event._depgraph['pn'][pn]['summary']

            if 'license' in event._depgraph['pn'][pn]:
                recipe_info['license'] = event._depgraph['pn'][pn]['license']

            if 'description' in event._depgraph['pn'][pn]:
                recipe_info['description'] = event._depgraph['pn'][pn]['description']

            if 'section' in event._depgraph['pn'][pn]:
                recipe_info['section'] = event._depgraph['pn'][pn]['section']

            if 'homepage' in event._depgraph['pn'][pn]:
                recipe_info['homepage'] = event._depgraph['pn'][pn]['homepage']

            if 'bugtracker' in event._depgraph['pn'][pn]:
                recipe_info['bugtracker'] = event._depgraph['pn'][pn]['bugtracker']

            recipe_info['file_path'] = file_name
            recipe_info['pathflags'] = pathflags

            if recipe_info['file_path'].startswith(recipe_info['layer_version'].local_path):
                recipe_info['file_path'] = recipe_info['file_path'][len(recipe_info['layer_version'].local_path):].lstrip("/")
            else:
                raise RuntimeError("Recipe file path %s is not under layer version at %s" % (recipe_info['file_path'], recipe_info['layer_version'].local_path))

            recipe = self.orm_wrapper.get_update_recipe_object(recipe_info)
            recipe.is_image = False
            if 'inherits' in event._depgraph['pn'][pn].keys():
                for cls in event._depgraph['pn'][pn]['inherits']:
                    if cls.endswith('/image.bbclass'):
                        recipe.is_image = True
                        recipe_info['is_image'] = True
                        # Save the is_image state to the relevant recipe objects
                        self.orm_wrapper.get_update_recipe_object(recipe_info)
                        break
            if recipe.is_image:
                for t in self.internal_state['targets']:
                    if pn == t.target:
                        t.is_image = True
                        t.save()
            self.internal_state['recipes'][pn] = recipe

        # we'll not get recipes for key w/ values listed in ASSUME_PROVIDED

        assume_provided = self.server.runCommand(["getVariable", "ASSUME_PROVIDED"])[0].split()

        # save recipe dependency
        # buildtime
        recipedeps_objects = []
        for recipe in event._depgraph['depends']:
           target = self.internal_state['recipes'][recipe]
           for dep in event._depgraph['depends'][recipe]:
                if dep in assume_provided:
                    continue
                via = None
                if 'providermap' in event._depgraph and dep in event._depgraph['providermap']:
                    deprecipe = event._depgraph['providermap'][dep][0]
                    dependency = self.internal_state['recipes'][deprecipe]
                    via = Provides.objects.get_or_create(name=dep,
                                                         recipe=dependency)[0]
                elif dep in self.internal_state['recipes']:
                    dependency = self.internal_state['recipes'][dep]
                else:
                    errormsg.append("  stpd: KeyError saving recipe dependency for %s, %s \n" % (recipe, dep))
                    continue
                recipe_dep = Recipe_Dependency(recipe=target,
                                               depends_on=dependency,
                                               via=via,
                                               dep_type=Recipe_Dependency.TYPE_DEPENDS)
                recipedeps_objects.append(recipe_dep)

        Recipe_Dependency.objects.bulk_create(recipedeps_objects)

        # save all task information
        def _save_a_task(taskdesc):
            spec = re.split(r'\.', taskdesc)
            pn = ".".join(spec[0:-1])
            taskname = spec[-1]
            e = event
            e.taskname = pn
            recipe = self.internal_state['recipes'][pn]
            task_info = self._get_task_information(e, recipe)
            task_info['task_name'] = taskname
            task_obj = self.orm_wrapper.get_update_task_object(task_info)
            return task_obj

        # create tasks
        tasks = {}
        for taskdesc in event._depgraph['tdepends']:
            tasks[taskdesc] = _save_a_task(taskdesc)

        # create dependencies between tasks
        taskdeps_objects = []
        for taskdesc in event._depgraph['tdepends']:
            target = tasks[taskdesc]
            for taskdep in event._depgraph['tdepends'][taskdesc]:
                if taskdep not in tasks:
                    # Fetch tasks info is not collected previously
                    dep = _save_a_task(taskdep)
                else:
                    dep = tasks[taskdep]
                taskdeps_objects.append(Task_Dependency( task = target, depends_on = dep ))
        Task_Dependency.objects.bulk_create(taskdeps_objects)

        if errormsg:
            logger.warning("buildinfohelper: dependency info not identify recipes: \n%s", "".join(errormsg))


    def store_build_package_information(self, event):
        self._ensure_build()

        package_info = BuildInfoHelper._get_data_from_event(event)
        self.orm_wrapper.save_build_package_information(
            self.internal_state['build'],
            package_info,
            self.internal_state['recipes'],
            built_package=True)

        self.orm_wrapper.save_build_package_information(
            self.internal_state['build'],
            package_info,
            self.internal_state['recipes'],
            built_package=False)

    def _store_build_done(self, errorcode):
        logger.info("Build exited with errorcode %d", errorcode)

        if not self.brbe:
            return

        br_id, be_id = self.brbe.split(":")

        br = BuildRequest.objects.get(pk = br_id)

        # if we're 'done' because we got cancelled update the build outcome
        if br.state == BuildRequest.REQ_CANCELLING:
            logger.info("Build cancelled")
            br.build.outcome = Build.CANCELLED
            br.build.save()
            self.internal_state['build'] = br.build
            errorcode = 0

        if errorcode == 0:
            # request archival of the project artifacts
            br.state = BuildRequest.REQ_COMPLETED
        else:
            br.state = BuildRequest.REQ_FAILED
        br.save()

        be = BuildEnvironment.objects.get(pk = be_id)
        be.lock = BuildEnvironment.LOCK_FREE
        be.save()
        signal_runbuilds()

    def store_log_error(self, text):
        mockevent = MockEvent()
        mockevent.levelno = formatter.ERROR
        mockevent.msg = text
        mockevent.pathname = '-- None'
        mockevent.lineno = LogMessage.ERROR
        self.store_log_event(mockevent)

    def store_log_exception(self, text, backtrace = ""):
        mockevent = MockEvent()
        mockevent.levelno = -1
        mockevent.msg = text
        mockevent.pathname = backtrace
        mockevent.lineno = -1
        self.store_log_event(mockevent)

    def store_log_event(self, event,cli_backlog=True):
        self._ensure_build()

        if event.levelno < formatter.WARNING:
            return

        # early return for CLI builds
        if cli_backlog and self.brbe is None:
            if not 'backlog' in self.internal_state:
                self.internal_state['backlog'] = []
            self.internal_state['backlog'].append(event)
            return

        if 'backlog' in self.internal_state:
            # if we have a backlog of events, do our best to save them here
            if self.internal_state['backlog']:
                tempevent = self.internal_state['backlog'].pop()
                logger.debug("buildinfohelper: Saving stored event %s "
                             % tempevent)
                self.store_log_event(tempevent,cli_backlog)
            else:
                logger.info("buildinfohelper: All events saved")
                del self.internal_state['backlog']

        log_information = {}
        log_information['build'] = self.internal_state['build']
        if event.levelno == formatter.CRITICAL:
            log_information['level'] = LogMessage.CRITICAL
        elif event.levelno == formatter.ERROR:
            log_information['level'] = LogMessage.ERROR
        elif event.levelno == formatter.WARNING:
            log_information['level'] = LogMessage.WARNING
        elif event.levelno == -2:   # toaster self-logging
            log_information['level'] = -2
        else:
            log_information['level'] = LogMessage.INFO

        log_information['message'] = event.getMessage()
        log_information['pathname'] = event.pathname
        log_information['lineno'] = event.lineno
        logger.info("Logging error 2: %s", log_information)

        self.orm_wrapper.create_logmessage(log_information)

    def _get_filenames_from_image_license(self, image_license_manifest_path):
        """
        Find the FILES line in the image_license.manifest file,
        which has the basenames of the bzImage and modules files
        in this format:
        FILES: bzImage--4.4.11+git0+3a5f494784_53e84104c5-r0-qemux86-20160603165040.bin modules--4.4.11+git0+3a5f494784_53e84104c5-r0-qemux86-20160603165040.tgz
        """
        files = []
        with open(image_license_manifest_path) as image_license:
            for line in image_license:
                if line.startswith('FILES'):
                    files_str = line.split(':')[1].strip()
                    files_str = re.sub(r' {2,}', ' ', files_str)

                    # ignore lines like "FILES:" with no filenames
                    if files_str:
                        files += files_str.split(' ')
        return files

    def _endswith(self, str_to_test, endings):
        """
        Returns True if str ends with one of the strings in the list
        endings, False otherwise
        """
        endswith = False
        for ending in endings:
            if str_to_test.endswith(ending):
                endswith = True
                break
        return endswith

    def scan_task_artifacts(self, event):
        """
        The 'TaskArtifacts' event passes the manifest file content for the
        tasks 'do_deploy', 'do_image_complete', 'do_populate_sdk', and
        'do_populate_sdk_ext'. The first two will be implemented later.
        """
        task_vars = BuildInfoHelper._get_data_from_event(event)
        task_name = task_vars['task'][task_vars['task'].find(':')+1:]
        task_artifacts = task_vars['artifacts']

        if task_name in ['do_populate_sdk', 'do_populate_sdk_ext']:
            targets = [target for target in self.internal_state['targets'] \
                if target.task == task_name[3:]]
            if not targets:
                logger.warning("scan_task_artifacts: SDK targets not found: %s\n", task_name)
                return
            for artifact_path in task_artifacts:
                if not os.path.isfile(artifact_path):
                    logger.warning("scan_task_artifacts: artifact file not found: %s\n", artifact_path)
                    continue
                for target in targets:
                    # don't record the file if it's already been added
                    # to this target
                    matching_files = TargetSDKFile.objects.filter(
                        target=target, file_name=artifact_path)
                    if matching_files.count() == 0:
                        artifact_size = os.stat(artifact_path).st_size
                        self.orm_wrapper.save_target_sdk_file(
                            target, artifact_path, artifact_size)

    def _get_image_files(self, deploy_dir_image, image_name, image_file_extensions):
        """
        Find files in deploy_dir_image whose basename starts with the
        string image_name and ends with one of the strings in
        image_file_extensions.

        Returns a list of file dictionaries like

        [
            {
                'path': '/path/to/image/file',
                'size': <file size in bytes>
            }
        ]
        """
        image_files = []

        for dirpath, _, filenames in os.walk(deploy_dir_image):
            for filename in filenames:
                if filename.startswith(image_name) and \
                self._endswith(filename, image_file_extensions):
                    image_file_path = os.path.join(dirpath, filename)
                    image_file_size = os.stat(image_file_path).st_size

                    image_files.append({
                        'path': image_file_path,
                        'size': image_file_size
                    })

        return image_files

    def scan_image_artifacts(self):
        """
        Scan for built image artifacts in DEPLOY_DIR_IMAGE and associate them
        with a Target object in self.internal_state['targets'].

        We have two situations to handle:

        1. This is the first time a target + machine has been built, so
        add files from the DEPLOY_DIR_IMAGE to the target.

        OR

        2. There are no new files for the target (they were already produced by
        a previous build), so copy them from the most recent previous build with
        the same target, task and machine.
        """
        deploy_dir_image = \
            self.server.runCommand(['getVariable', 'DEPLOY_DIR_IMAGE'])[0]

        # if there's no DEPLOY_DIR_IMAGE, there aren't going to be
        # any image artifacts, so we can return immediately
        if not deploy_dir_image:
            return

        buildname = self.server.runCommand(['getVariable', 'BUILDNAME'])[0]
        machine = self.server.runCommand(['getVariable', 'MACHINE'])[0]

        # location of the manifest files for this build;
        # note that this file is only produced if an image is produced
        license_directory = \
            self.server.runCommand(['getVariable', 'LICENSE_DIRECTORY'])[0]

        # file name extensions for image files
        image_file_extensions_unique = {}
        image_fstypes = self.server.runCommand(
            ['getVariable', 'IMAGE_FSTYPES'])[0]
        if image_fstypes is not None:
            image_types_str = image_fstypes.strip()
            image_file_extensions = re.sub(r' {2,}', ' ', image_types_str)
            image_file_extensions_unique = set(image_file_extensions.split(' '))

        targets = self.internal_state['targets']

        # filter out anything which isn't an image target
        image_targets = [target for target in targets if target.is_image]

        if len(image_targets) > 0:
            #if there are image targets retrieve image_name
            image_name = self.server.runCommand(['getVariable', 'IMAGE_NAME'])[0]
            if not image_name:
                #When build target is an image and image_name is not found as an environment variable
                logger.info("IMAGE_NAME not found, extracting from bitbake command")
                cmd = self.server.runCommand(['getVariable','BB_CMDLINE'])[0]
                #filter out tokens that are command line options
                cmd = [token for token in cmd if not token.startswith('-')]
                image_name = cmd[1].split(':', 1)[0] # remove everything after : in image name
                logger.info("IMAGE_NAME found as : %s " % image_name)

        for image_target in image_targets:
            # this is set to True if we find at least one file relating to
            # this target; if this remains False after the scan, we copy the
            # files from the most-recent Target with the same target + machine
            # onto this Target instead
            has_files = False

            # we construct this because by the time we reach
            # BuildCompleted, this has reset to
            # 'defaultpkgname-<MACHINE>-<BUILDNAME>';
            # we need to change it to
            # <TARGET>-<MACHINE>-<BUILDNAME>
            real_image_name = re.sub(r'^defaultpkgname', image_target.target,
                image_name)

            image_license_manifest_path = os.path.join(
                license_directory,
                real_image_name,
                'image_license.manifest')

            image_package_manifest_path = os.path.join(
                license_directory,
                real_image_name,
                'image_license.manifest')

            # if image_license.manifest exists, we can read the names of
            # bzImage, modules etc. files for this build from it, then look for
            # them in the DEPLOY_DIR_IMAGE; note that this file is only produced
            # if an image file was produced
            if os.path.isfile(image_license_manifest_path):
                has_files = True

                basenames = self._get_filenames_from_image_license(
                    image_license_manifest_path)

                for basename in basenames:
                    artifact_path = os.path.join(deploy_dir_image, basename)
                    if not os.path.exists(artifact_path):
                        logger.warning("artifact %s doesn't exist, skipping" % artifact_path)
                        continue
                    artifact_size = os.stat(artifact_path).st_size

                    # note that the artifact will only be saved against this
                    # build if it hasn't been already
                    self.orm_wrapper.save_target_kernel_file(image_target,
                        artifact_path, artifact_size)

                # store the license manifest path on the target
                # (this file is also created any time an image file is created)
                license_manifest_path = os.path.join(license_directory,
                    real_image_name, 'license.manifest')

                self.orm_wrapper.update_target_set_license_manifest(
                    image_target, license_manifest_path)

                # store the package manifest path on the target (this file
                # is created any time an image file is created)
                package_manifest_path = os.path.join(deploy_dir_image,
                    real_image_name + '.rootfs.manifest')

                if os.path.exists(package_manifest_path):
                    self.orm_wrapper.update_target_set_package_manifest(
                        image_target, package_manifest_path)

            # scan the directory for image files relating to this build
            # (via real_image_name); note that we don't have to set
            # has_files = True, as searching for the license manifest file
            # will already have set it to true if at least one image file was
            # produced; note that the real_image_name includes BUILDNAME, which
            # in turn includes a timestamp; so if no files were produced for
            # this timestamp (i.e. the build reused existing image files already
            # in the directory), no files will be recorded against this target
            image_files = self._get_image_files(deploy_dir_image,
                real_image_name, image_file_extensions_unique)

            for image_file in image_files:
                self.orm_wrapper.save_target_image_file_information(
                    image_target, image_file['path'], image_file['size'])

            if not has_files:
                # copy image files and build artifacts from the
                # most-recently-built Target with the
                # same target + machine as this Target; also copy the license
                # manifest path, as that is not treated as an artifact and needs
                # to be set separately
                similar_target = \
                    self.orm_wrapper.get_similar_target_with_image_files(
                        image_target)

                if similar_target:
                    logger.info('image artifacts for target %s cloned from ' \
                        'target %s' % (image_target.pk, similar_target.pk))
                    self.orm_wrapper.clone_image_artifacts(similar_target,
                        image_target)

    def _get_sdk_targets(self):
        """
        Return targets which could generate SDK artifacts, i.e.
        "do_populate_sdk" and "do_populate_sdk_ext".
        """
        return [target for target in self.internal_state['targets'] \
            if target.task in ['populate_sdk', 'populate_sdk_ext']]

    def scan_sdk_artifacts(self, event):
        """
        Note that we have to intercept an SDKArtifactInfo event from
        toaster.bbclass (via toasterui) to get hold of the SDK variables we
        need to be able to scan for files accurately: this is because
        variables like TOOLCHAIN_OUTPUTNAME have reset to None by the time
        BuildCompleted is fired by bitbake, so we have to get those values
        while the build is still in progress.

        For populate_sdk_ext, this runs twice, with two different
        TOOLCHAIN_OUTPUTNAME settings, each of which will capture some of the
        files in the SDK output directory.
        """
        sdk_vars = BuildInfoHelper._get_data_from_event(event)
        toolchain_outputname = sdk_vars['TOOLCHAIN_OUTPUTNAME']

        # targets which might have created SDK artifacts
        sdk_targets = self._get_sdk_targets()

        # location of SDK artifacts
        tmpdir = self.server.runCommand(['getVariable', 'TMPDIR'])[0]
        sdk_dir = os.path.join(tmpdir, 'deploy', 'sdk')

        # all files in the SDK directory
        artifacts = []
        for dir_path, _, filenames in os.walk(sdk_dir):
            for filename in filenames:
                full_path = os.path.join(dir_path, filename)
                if not os.path.islink(full_path):
                    artifacts.append(full_path)

        for sdk_target in sdk_targets:
            # find files in the SDK directory which haven't already been
            # recorded against a Target and whose basename matches
            # TOOLCHAIN_OUTPUTNAME
            for artifact_path in artifacts:
                basename = os.path.basename(artifact_path)

                toolchain_match = basename.startswith(toolchain_outputname)

                # files which match the name of the target which produced them;
                # for example,
                # poky-glibc-x86_64-core-image-sato-i586-toolchain-ext-2.1+snapshot.sh
                target_match = re.search(sdk_target.target, basename)

                # targets which produce "*-nativesdk-*" files
                is_ext_sdk_target = sdk_target.task in \
                    ['do_populate_sdk_ext', 'populate_sdk_ext']

                # SDK files which don't match the target name, i.e.
                # x86_64-nativesdk-libc.*
                # poky-glibc-x86_64-buildtools-tarball-i586-buildtools-nativesdk-standalone-2.1+snapshot*
                is_ext_sdk_file = re.search('-nativesdk-', basename)

                file_from_target = (toolchain_match and target_match) or \
                    (is_ext_sdk_target and is_ext_sdk_file)

                if file_from_target:
                    # don't record the file if it's already been added to this
                    # target
                    matching_files = TargetSDKFile.objects.filter(
                        target=sdk_target, file_name=artifact_path)

                    if matching_files.count() == 0:
                        artifact_size = os.stat(artifact_path).st_size

                        self.orm_wrapper.save_target_sdk_file(
                            sdk_target, artifact_path, artifact_size)

    def clone_required_sdk_artifacts(self):
        """
        If an SDK target doesn't have any SDK artifacts, this means that
        the postfuncs of populate_sdk or populate_sdk_ext didn't fire, which
        in turn means that the targets of this build didn't generate any new
        artifacts.

        In this case, clone SDK artifacts for targets in the current build
        from existing targets for this build.
        """
        sdk_targets = self._get_sdk_targets()
        for sdk_target in sdk_targets:
            # only clone for SDK targets which have no TargetSDKFiles yet
            if sdk_target.targetsdkfile_set.all().count() == 0:
                similar_target = \
                    self.orm_wrapper.get_similar_target_with_sdk_files(
                        sdk_target)
                if similar_target:
                    logger.info('SDK artifacts for target %s cloned from ' \
                        'target %s' % (sdk_target.pk, similar_target.pk))
                    self.orm_wrapper.clone_sdk_artifacts(similar_target,
                        sdk_target)

    def close(self, errorcode):
        self._store_build_done(errorcode)

        if 'backlog' in self.internal_state:
            # we save missed events in the database for the current build
            tempevent = self.internal_state['backlog'].pop()
            # Do not skip command line build events
            self.store_log_event(tempevent,False)


        # unset the brbe; this is to prevent subsequent command-line builds
        # being incorrectly attached to the previous Toaster-triggered build;
        # see https://bugzilla.yoctoproject.org/show_bug.cgi?id=9021
        self.brbe = None

        # unset the internal Build object to prevent it being reused for the
        # next build
        self.internal_state['build'] = None
