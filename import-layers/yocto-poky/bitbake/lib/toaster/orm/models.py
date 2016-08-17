#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013        Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

from __future__ import unicode_literals

from django.db import models, IntegrityError
from django.db.models import F, Q, Avg, Max, Sum
from django.utils import timezone
from django.utils.encoding import force_bytes

from django.core.urlresolvers import reverse

from django.core import validators
from django.conf import settings
import django.db.models.signals

import os.path
import re
import itertools

import logging
logger = logging.getLogger("toaster")

if 'sqlite' in settings.DATABASES['default']['ENGINE']:
    from django.db import transaction, OperationalError
    from time import sleep

    _base_save = models.Model.save
    def save(self, *args, **kwargs):
        while True:
            try:
                with transaction.atomic():
                    return _base_save(self, *args, **kwargs)
            except OperationalError as err:
                if 'database is locked' in str(err):
                    logger.warning("%s, model: %s, args: %s, kwargs: %s",
                                   err, self.__class__, args, kwargs)
                    sleep(0.5)
                    continue
                raise

    models.Model.save = save

    # HACK: Monkey patch Django to fix 'database is locked' issue

    from django.db.models.query import QuerySet
    _base_insert = QuerySet._insert
    def _insert(self,  *args, **kwargs):
        with transaction.atomic(using=self.db, savepoint=False):
            return _base_insert(self, *args, **kwargs)
    QuerySet._insert = _insert

    from django.utils import six
    def _create_object_from_params(self, lookup, params):
        """
        Tries to create an object using passed params.
        Used by get_or_create and update_or_create
        """
        try:
            obj = self.create(**params)
            return obj, True
        except IntegrityError:
            exc_info = sys.exc_info()
            try:
                return self.get(**lookup), False
            except self.model.DoesNotExist:
                pass
            six.reraise(*exc_info)

    QuerySet._create_object_from_params = _create_object_from_params

    # end of HACK

class GitURLValidator(validators.URLValidator):
    import re
    regex = re.compile(
        r'^(?:ssh|git|http|ftp)s?://'  # http:// or https://
        r'(?:(?:[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?\.)+(?:[A-Z]{2,6}\.?|[A-Z0-9-]{2,}\.?)|'  # domain...
        r'localhost|'  # localhost...
        r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}|'  # ...or ipv4
        r'\[?[A-F0-9]*:[A-F0-9:]+\]?)'  # ...or ipv6
        r'(?::\d+)?'  # optional port
        r'(?:/?|[/?]\S+)$', re.IGNORECASE)

def GitURLField(**kwargs):
    r = models.URLField(**kwargs)
    for i in xrange(len(r.validators)):
        if isinstance(r.validators[i], validators.URLValidator):
            r.validators[i] = GitURLValidator()
    return r


class ToasterSetting(models.Model):
    name = models.CharField(max_length=63)
    helptext = models.TextField()
    value = models.CharField(max_length=255)

    def __unicode__(self):
        return "Setting %s = %s" % (self.name, self.value)

class ProjectManager(models.Manager):
    def create_project(self, name, release):
        if release is not None:
            prj = self.model(name = name, bitbake_version = release.bitbake_version, release = release)
        else:
            prj = self.model(name = name, bitbake_version = None, release = None)

        prj.save()

        for defaultconf in ToasterSetting.objects.filter(name__startswith="DEFCONF_"):
            name = defaultconf.name[8:]
            ProjectVariable.objects.create( project = prj,
                name = name,
                value = defaultconf.value)

        if release is None:
            return prj

        for rdl in release.releasedefaultlayer_set.all():
            try:
                lv = Layer_Version.objects.filter(layer__name = rdl.layer_name, up_branch__name = release.branch_name)[0].get_equivalents_wpriority(prj)[0]
                ProjectLayer.objects.create( project = prj,
                        layercommit = lv,
                        optional = False )
            except IndexError:
                # we may have no valid layer version objects, and that's ok
                pass

        return prj

    # return single object with is_default = True
    def get_or_create_default_project(self):
        projects = super(ProjectManager, self).filter(is_default = True)

        if len(projects) > 1:
            raise Exception('Inconsistent project data: multiple ' +
                            'default projects (i.e. with is_default=True)')
        elif len(projects) < 1:
            options = {
                'name': 'Command line builds',
                'short_description': 'Project for builds started outside Toaster',
                'is_default': True
            }
            project = Project.objects.create(**options)
            project.save()

            return project
        else:
            return projects[0]

class Project(models.Model):
    search_allowed_fields = ['name', 'short_description', 'release__name', 'release__branch_name']
    name = models.CharField(max_length=100)
    short_description = models.CharField(max_length=50, blank=True)
    bitbake_version = models.ForeignKey('BitbakeVersion', null=True)
    release     = models.ForeignKey("Release", null=True)
    created     = models.DateTimeField(auto_now_add = True)
    updated     = models.DateTimeField(auto_now = True)
    # This is a horrible hack; since Toaster has no "User" model available when
    # running in interactive mode, we can't reference the field here directly
    # Instead, we keep a possible null reference to the User id, as not to force
    # hard links to possibly missing models
    user_id     = models.IntegerField(null = True)
    objects     = ProjectManager()

    # set to True for the project which is the default container
    # for builds initiated by the command line etc.
    is_default  = models.BooleanField(default = False)

    def __unicode__(self):
        return "%s (Release %s, BBV %s)" % (self.name, self.release, self.bitbake_version)

    def get_current_machine_name(self):
        try:
            return self.projectvariable_set.get(name="MACHINE").value
        except (ProjectVariable.DoesNotExist,IndexError):
            return None;

    def get_number_of_builds(self):
        """Return the number of builds which have ended"""

        return self.build_set.exclude(
            Q(outcome=Build.IN_PROGRESS) |
            Q(outcome=Build.CANCELLED)
        ).count()

    def get_last_build_id(self):
        try:
            return Build.objects.filter( project = self.id ).order_by('-completed_on')[0].id
        except (Build.DoesNotExist,IndexError):
            return( -1 )

    def get_last_outcome(self):
        build_id = self.get_last_build_id
        if (-1 == build_id):
            return( "" )
        try:
            return Build.objects.filter( id = self.get_last_build_id )[ 0 ].outcome
        except (Build.DoesNotExist,IndexError):
            return( "not_found" )

    def get_last_target(self):
        build_id = self.get_last_build_id
        if (-1 == build_id):
            return( "" )
        try:
            return Target.objects.filter(build = build_id)[0].target
        except (Target.DoesNotExist,IndexError):
            return( "not_found" )

    def get_last_errors(self):
        build_id = self.get_last_build_id
        if (-1 == build_id):
            return( 0 )
        try:
            return Build.objects.filter(id = build_id)[ 0 ].errors.count()
        except (Build.DoesNotExist,IndexError):
            return( "not_found" )

    def get_last_warnings(self):
        build_id = self.get_last_build_id
        if (-1 == build_id):
            return( 0 )
        try:
            return Build.objects.filter(id = build_id)[ 0 ].warnings.count()
        except (Build.DoesNotExist,IndexError):
            return( "not_found" )

    def get_last_build_extensions(self):
        """
        Get list of file name extensions for images produced by the most
        recent build
        """
        last_build = Build.objects.get(pk = self.get_last_build_id())
        return last_build.get_image_file_extensions()

    def get_last_imgfiles(self):
        build_id = self.get_last_build_id
        if (-1 == build_id):
            return( "" )
        try:
            return Variable.objects.filter(build = build_id, variable_name = "IMAGE_FSTYPES")[ 0 ].variable_value
        except (Variable.DoesNotExist,IndexError):
            return( "not_found" )

    def get_all_compatible_layer_versions(self):
        """ Returns Queryset of all Layer_Versions which are compatible with
        this project"""
        queryset = None

        # guard on release, as it can be null
        if self.release:
            queryset = Layer_Version.objects.filter(
                (Q(up_branch__name=self.release.branch_name) &
                 Q(build=None) &
                 Q(project=None)) |
                 Q(project=self))
        else:
            queryset = Layer_Version.objects.none()

        return queryset

    def get_project_layer_versions(self, pk=False):
        """ Returns the Layer_Versions currently added to this project """
        layer_versions = self.projectlayer_set.all().values_list('layercommit',
                                                                 flat=True)

        if pk is False:
            return Layer_Version.objects.filter(pk__in=layer_versions)
        else:
            return layer_versions


    def get_available_machines(self):
        """ Returns QuerySet of all Machines which are provided by the
        Layers currently added to the Project """
        queryset = Machine.objects.filter(
            layer_version__in=self.get_project_layer_versions())

        return queryset

    def get_all_compatible_machines(self):
        """ Returns QuerySet of all the compatible machines available to the
        project including ones from Layers not currently added """
        queryset = Machine.objects.filter(
            layer_version__in=self.get_all_compatible_layer_versions())

        return queryset

    def get_available_recipes(self):
        """ Returns QuerySet of all the recipes that are provided by layers
        added to this project """
        queryset = Recipe.objects.filter(
            layer_version__in=self.get_project_layer_versions())

        return queryset

    def get_all_compatible_recipes(self):
        """ Returns QuerySet of all the compatible Recipes available to the
        project including ones from Layers not currently added """
        queryset = Recipe.objects.filter(
            layer_version__in=self.get_all_compatible_layer_versions()).exclude(name__exact='')

        return queryset


    def schedule_build(self):
        from bldcontrol.models import BuildRequest, BRTarget, BRLayer, BRVariable, BRBitbake
        br = BuildRequest.objects.create(project = self)
        try:

            BRBitbake.objects.create(req = br,
                giturl = self.bitbake_version.giturl,
                commit = self.bitbake_version.branch,
                dirpath = self.bitbake_version.dirpath)

            for l in self.projectlayer_set.all().order_by("pk"):
                commit = l.layercommit.get_vcs_reference()
                print("ii Building layer ", l.layercommit.layer.name, " at vcs point ", commit)
                BRLayer.objects.create(req = br, name = l.layercommit.layer.name, giturl = l.layercommit.layer.vcs_url, commit = commit, dirpath = l.layercommit.dirpath, layer_version=l.layercommit)

            br.state = BuildRequest.REQ_QUEUED
            now = timezone.now()
            br.build = Build.objects.create(project = self,
                                completed_on=now,
                                started_on=now,
                                )
            for t in self.projecttarget_set.all():
                BRTarget.objects.create(req = br, target = t.target, task = t.task)
                Target.objects.create(build = br.build, target = t.target, task = t.task)

            for v in self.projectvariable_set.all():
                BRVariable.objects.create(req = br, name = v.name, value = v.value)


            try:
                br.build.machine = self.projectvariable_set.get(name = 'MACHINE').value
                br.build.save()
            except ProjectVariable.DoesNotExist:
                pass
            br.save()
        except Exception:
            # revert the build request creation since we're not done cleanly
            br.delete()
            raise
        return br

class Build(models.Model):
    SUCCEEDED = 0
    FAILED = 1
    IN_PROGRESS = 2
    CANCELLED = 3

    BUILD_OUTCOME = (
        (SUCCEEDED, 'Succeeded'),
        (FAILED, 'Failed'),
        (IN_PROGRESS, 'In Progress'),
        (CANCELLED, 'Cancelled'),
    )

    search_allowed_fields = ['machine', 'cooker_log_path', "target__target", "target__target_image_file__file_name"]

    project = models.ForeignKey(Project)            # must have a project
    machine = models.CharField(max_length=100)
    distro = models.CharField(max_length=100)
    distro_version = models.CharField(max_length=100)
    started_on = models.DateTimeField()
    completed_on = models.DateTimeField()
    outcome = models.IntegerField(choices=BUILD_OUTCOME, default=IN_PROGRESS)
    cooker_log_path = models.CharField(max_length=500)
    build_name = models.CharField(max_length=100)
    bitbake_version = models.CharField(max_length=50)

    @staticmethod
    def get_recent(project=None):
        """
        Return recent builds as a list; if project is set, only return
        builds for that project
        """

        builds = Build.objects.all()

        if project:
            builds = builds.filter(project=project)

        finished_criteria = \
                Q(outcome=Build.SUCCEEDED) | \
                Q(outcome=Build.FAILED) | \
                Q(outcome=Build.CANCELLED)

        recent_builds = list(itertools.chain(
            builds.filter(outcome=Build.IN_PROGRESS).order_by("-started_on"),
            builds.filter(finished_criteria).order_by("-completed_on")[:3]
        ))

        # add percentage done property to each build; this is used
        # to show build progress in mrb_section.html
        for build in recent_builds:
            build.percentDone = build.completeper()

        return recent_builds

    def completeper(self):
        tf = Task.objects.filter(build = self)
        tfc = tf.count()
        if tfc > 0:
            completeper = tf.exclude(order__isnull=True).count()*100/tfc
        else:
            completeper = 0
        return completeper

    def eta(self):
        eta = timezone.now()
        completeper = self.completeper()
        if self.completeper() > 0:
            eta += ((eta - self.started_on)*(100-completeper))/completeper
        return eta

    def get_image_file_extensions(self):
        """
        Get list of file name extensions for images produced by this build
        """
        targets = Target.objects.filter(build_id = self.id)
        extensions = []

        # pattern to match against file path for building extension string
        pattern = re.compile('\.([^\.]+?)$')

        for target in targets:
            if (not target.is_image):
                continue

            target_image_files = Target_Image_File.objects.filter(target_id = target.id)

            for target_image_file in target_image_files:
                file_name = os.path.basename(target_image_file.file_name)
                suffix = ''

                continue_matching = True

                # incrementally extract the suffix from the file path,
                # checking it against the list of valid suffixes at each
                # step; if the path is stripped of all potential suffix
                # parts without matching a valid suffix, this returns all
                # characters after the first '.' in the file name
                while continue_matching:
                    matches = pattern.search(file_name)

                    if None == matches:
                        continue_matching = False
                        suffix = re.sub('^\.', '', suffix)
                        continue
                    else:
                        suffix = matches.group(1) + suffix

                    if suffix in Target_Image_File.SUFFIXES:
                        continue_matching = False
                        continue
                    else:
                        # reduce the file name and try to find the next
                        # segment from the path which might be part
                        # of the suffix
                        file_name = re.sub('.' + matches.group(1), '', file_name)
                        suffix = '.' + suffix

                if not suffix in extensions:
                    extensions.append(suffix)

        return ', '.join(extensions)

    def get_sorted_target_list(self):
        tgts = Target.objects.filter(build_id = self.id).order_by( 'target' );
        return( tgts );

    def get_recipes(self):
        """
        Get the recipes related to this build;
        note that the related layer versions and layers are also prefetched
        by this query, as this queryset can be sorted by these objects in the
        build recipes view; prefetching them here removes the need
        for another query in that view
        """
        layer_versions = Layer_Version.objects.filter(build=self)
        criteria = Q(layer_version__id__in=layer_versions)
        return Recipe.objects.filter(criteria) \
                             .select_related('layer_version', 'layer_version__layer')

    def get_image_recipes(self):
        """
        Returns a list of image Recipes (custom and built-in) related to this
        build, sorted by name; note that this has to be done in two steps, as
        there's no way to get all the custom image recipes and image recipes
        in one query
        """
        custom_image_recipes = self.get_custom_image_recipes()
        custom_image_recipe_names = custom_image_recipes.values_list('name', flat=True)

        not_custom_image_recipes = ~Q(name__in=custom_image_recipe_names) & \
                                   Q(is_image=True)

        built_image_recipes = self.get_recipes().filter(not_custom_image_recipes)

        # append to the custom image recipes and sort
        customisable_image_recipes = list(
            itertools.chain(custom_image_recipes, built_image_recipes)
        )

        return sorted(customisable_image_recipes, key=lambda recipe: recipe.name)

    def get_custom_image_recipes(self):
        """
        Returns a queryset of CustomImageRecipes related to this build,
        sorted by name
        """
        built_recipe_names = self.get_recipes().values_list('name', flat=True)
        criteria = Q(name__in=built_recipe_names) & Q(project=self.project)
        queryset = CustomImageRecipe.objects.filter(criteria).order_by('name')
        return queryset

    def get_outcome_text(self):
        return Build.BUILD_OUTCOME[int(self.outcome)][1]

    @property
    def failed_tasks(self):
        """ Get failed tasks for the build """
        tasks = self.task_build.all()
        return tasks.filter(order__gt=0, outcome=Task.OUTCOME_FAILED)

    @property
    def errors(self):
        return (self.logmessage_set.filter(level=LogMessage.ERROR) |
                self.logmessage_set.filter(level=LogMessage.EXCEPTION) |
                self.logmessage_set.filter(level=LogMessage.CRITICAL))

    @property
    def warnings(self):
        return self.logmessage_set.filter(level=LogMessage.WARNING)

    @property
    def timespent(self):
        return self.completed_on - self.started_on

    @property
    def timespent_seconds(self):
        return self.timespent.total_seconds()

    @property
    def target_labels(self):
        """
        Sorted (a-z) "target1:task, target2, target3" etc. string for all
        targets in this build
        """
        targets = self.target_set.all()
        target_labels = [target.target +
                         (':' + target.task if target.task else '')
                         for target in targets]
        target_labels.sort()

        return target_labels

    def get_current_status(self):
        """
        get the status string from the build request if the build
        has one, or the text for the build outcome if it doesn't
        """

        from bldcontrol.models import BuildRequest

        build_request = None
        if hasattr(self, 'buildrequest'):
            build_request = self.buildrequest

        if (build_request
                and build_request.state != BuildRequest.REQ_INPROGRESS
                and self.outcome == Build.IN_PROGRESS):
            return self.buildrequest.get_state_display()
        else:
            return self.get_outcome_text()

    def __str__(self):
        return "%d %s %s" % (self.id, self.project, ",".join([t.target for t in self.target_set.all()]))


# an Artifact is anything that results from a Build, and may be of interest to the user, and is not stored elsewhere
class BuildArtifact(models.Model):
    build = models.ForeignKey(Build)
    file_name = models.FilePathField()
    file_size = models.IntegerField()

    def get_local_file_name(self):
        try:
            deploydir = Variable.objects.get(build = self.build, variable_name="DEPLOY_DIR").variable_value
            return  self.file_name[len(deploydir)+1:]
        except:
            raise

        return self.file_name

    def get_basename(self):
        return os.path.basename(self.file_name)

    def is_available(self):
        return self.build.buildrequest.environment.has_artifact(self.file_name)

class ProjectTarget(models.Model):
    project = models.ForeignKey(Project)
    target = models.CharField(max_length=100)
    task = models.CharField(max_length=100, null=True)

class Target(models.Model):
    search_allowed_fields = ['target', 'file_name']
    build = models.ForeignKey(Build)
    target = models.CharField(max_length=100)
    task = models.CharField(max_length=100, null=True)
    is_image = models.BooleanField(default = False)
    image_size = models.IntegerField(default=0)
    license_manifest_path = models.CharField(max_length=500, null=True)

    def package_count(self):
        return Target_Installed_Package.objects.filter(target_id__exact=self.id).count()

    def __unicode__(self):
        return self.target

class Target_Image_File(models.Model):
    # valid suffixes for image files produced by a build
    SUFFIXES = {
        'btrfs', 'cpio', 'cpio.gz', 'cpio.lz4', 'cpio.lzma', 'cpio.xz',
        'cramfs', 'elf', 'ext2', 'ext2.bz2', 'ext2.gz', 'ext2.lzma', 'ext4',
        'ext4.gz', 'ext3', 'ext3.gz', 'hddimg', 'iso', 'jffs2', 'jffs2.sum',
        'squashfs', 'squashfs-lzo', 'squashfs-xz', 'tar.bz2', 'tar.lz4',
        'tar.xz', 'tartar.gz', 'ubi', 'ubifs', 'vmdk'
    }

    target = models.ForeignKey(Target)
    file_name = models.FilePathField(max_length=254)
    file_size = models.IntegerField()

    @property
    def suffix(self):
        filename, suffix = os.path.splitext(self.file_name)
        suffix = suffix.lstrip('.')
        return suffix

class Target_File(models.Model):
    ITYPE_REGULAR = 1
    ITYPE_DIRECTORY = 2
    ITYPE_SYMLINK = 3
    ITYPE_SOCKET = 4
    ITYPE_FIFO = 5
    ITYPE_CHARACTER = 6
    ITYPE_BLOCK = 7
    ITYPES = ( (ITYPE_REGULAR ,'regular'),
        ( ITYPE_DIRECTORY ,'directory'),
        ( ITYPE_SYMLINK ,'symlink'),
        ( ITYPE_SOCKET ,'socket'),
        ( ITYPE_FIFO ,'fifo'),
        ( ITYPE_CHARACTER ,'character'),
        ( ITYPE_BLOCK ,'block'),
        )

    target = models.ForeignKey(Target)
    path = models.FilePathField()
    size = models.IntegerField()
    inodetype = models.IntegerField(choices = ITYPES)
    permission = models.CharField(max_length=16)
    owner = models.CharField(max_length=128)
    group = models.CharField(max_length=128)
    directory = models.ForeignKey('Target_File', related_name="directory_set", null=True)
    sym_target = models.ForeignKey('Target_File', related_name="symlink_set", null=True)


class Task(models.Model):

    SSTATE_NA = 0
    SSTATE_MISS = 1
    SSTATE_FAILED = 2
    SSTATE_RESTORED = 3

    SSTATE_RESULT = (
        (SSTATE_NA, 'Not Applicable'), # For rest of tasks, but they still need checking.
        (SSTATE_MISS, 'File not in cache'), # the sstate object was not found
        (SSTATE_FAILED, 'Failed'), # there was a pkg, but the script failed
        (SSTATE_RESTORED, 'Succeeded'), # successfully restored
    )

    CODING_NA = 0
    CODING_PYTHON = 2
    CODING_SHELL = 3

    TASK_CODING = (
        (CODING_NA, 'N/A'),
        (CODING_PYTHON, 'Python'),
        (CODING_SHELL, 'Shell'),
    )

    OUTCOME_NA = -1
    OUTCOME_SUCCESS = 0
    OUTCOME_COVERED = 1
    OUTCOME_CACHED = 2
    OUTCOME_PREBUILT = 3
    OUTCOME_FAILED = 4
    OUTCOME_EMPTY = 5

    TASK_OUTCOME = (
        (OUTCOME_NA, 'Not Available'),
        (OUTCOME_SUCCESS, 'Succeeded'),
        (OUTCOME_COVERED, 'Covered'),
        (OUTCOME_CACHED, 'Cached'),
        (OUTCOME_PREBUILT, 'Prebuilt'),
        (OUTCOME_FAILED, 'Failed'),
        (OUTCOME_EMPTY, 'Empty'),
    )

    TASK_OUTCOME_HELP = (
        (OUTCOME_SUCCESS, 'This task successfully completed'),
        (OUTCOME_COVERED, 'This task did not run because its output is provided by another task'),
        (OUTCOME_CACHED, 'This task restored output from the sstate-cache directory or mirrors'),
        (OUTCOME_PREBUILT, 'This task did not run because its outcome was reused from a previous build'),
        (OUTCOME_FAILED, 'This task did not complete'),
        (OUTCOME_EMPTY, 'This task has no executable content'),
        (OUTCOME_NA, ''),
    )

    search_allowed_fields = [ "recipe__name", "recipe__version", "task_name", "logfile" ]

    def __init__(self, *args, **kwargs):
        super(Task, self).__init__(*args, **kwargs)
        try:
            self._helptext = HelpText.objects.get(key=self.task_name, area=HelpText.VARIABLE, build=self.build).text
        except HelpText.DoesNotExist:
            self._helptext = None

    def get_related_setscene(self):
        return Task.objects.filter(task_executed=True, build = self.build, recipe = self.recipe, task_name=self.task_name+"_setscene")

    def get_outcome_text(self):
        return Task.TASK_OUTCOME[int(self.outcome) + 1][1]

    def get_outcome_help(self):
        return Task.TASK_OUTCOME_HELP[int(self.outcome)][1]

    def get_sstate_text(self):
        if self.sstate_result==Task.SSTATE_NA:
            return ''
        else:
            return Task.SSTATE_RESULT[int(self.sstate_result)][1]

    def get_executed_display(self):
        if self.task_executed:
            return "Executed"
        return "Not Executed"

    def get_description(self):
        return self._helptext

    build = models.ForeignKey(Build, related_name='task_build')
    order = models.IntegerField(null=True)
    task_executed = models.BooleanField(default=False) # True means Executed, False means Not/Executed
    outcome = models.IntegerField(choices=TASK_OUTCOME, default=OUTCOME_NA)
    sstate_checksum = models.CharField(max_length=100, blank=True)
    path_to_sstate_obj = models.FilePathField(max_length=500, blank=True)
    recipe = models.ForeignKey('Recipe', related_name='tasks')
    task_name = models.CharField(max_length=100)
    source_url = models.FilePathField(max_length=255, blank=True)
    work_directory = models.FilePathField(max_length=255, blank=True)
    script_type = models.IntegerField(choices=TASK_CODING, default=CODING_NA)
    line_number = models.IntegerField(default=0)

    # start/end times
    started = models.DateTimeField(null=True)
    ended = models.DateTimeField(null=True)

    # in seconds; this is stored to enable sorting
    elapsed_time = models.DecimalField(max_digits=8, decimal_places=2, null=True)

    # in bytes; note that disk_io is stored to enable sorting
    disk_io = models.IntegerField(null=True)
    disk_io_read = models.IntegerField(null=True)
    disk_io_write = models.IntegerField(null=True)

    # in seconds
    cpu_time_user = models.DecimalField(max_digits=8, decimal_places=2, null=True)
    cpu_time_system = models.DecimalField(max_digits=8, decimal_places=2, null=True)

    sstate_result = models.IntegerField(choices=SSTATE_RESULT, default=SSTATE_NA)
    message = models.CharField(max_length=240)
    logfile = models.FilePathField(max_length=255, blank=True)

    outcome_text = property(get_outcome_text)
    sstate_text  = property(get_sstate_text)

    def __unicode__(self):
        return "%d(%d) %s:%s" % (self.pk, self.build.pk, self.recipe.name, self.task_name)

    class Meta:
        ordering = ('order', 'recipe' ,)
        unique_together = ('build', 'recipe', 'task_name', )


class Task_Dependency(models.Model):
    task = models.ForeignKey(Task, related_name='task_dependencies_task')
    depends_on = models.ForeignKey(Task, related_name='task_dependencies_depends')

class Package(models.Model):
    search_allowed_fields = ['name', 'version', 'revision', 'recipe__name', 'recipe__version', 'recipe__license', 'recipe__layer_version__layer__name', 'recipe__layer_version__branch', 'recipe__layer_version__commit', 'recipe__layer_version__local_path', 'installed_name']
    build = models.ForeignKey('Build', null=True)
    recipe = models.ForeignKey('Recipe', null=True)
    name = models.CharField(max_length=100)
    installed_name = models.CharField(max_length=100, default='')
    version = models.CharField(max_length=100, blank=True)
    revision = models.CharField(max_length=32, blank=True)
    summary = models.TextField(blank=True)
    description = models.TextField(blank=True)
    size = models.IntegerField(default=0)
    installed_size = models.IntegerField(default=0)
    section = models.CharField(max_length=80, blank=True)
    license = models.CharField(max_length=80, blank=True)

    @property
    def is_locale_package(self):
        """ Returns True if this package is identifiable as a locale package """
        if self.name.find('locale') != -1:
            return True
        return False

    @property
    def is_packagegroup(self):
        """ Returns True is this package is identifiable as a packagegroup """
        if self.name.find('packagegroup') != -1:
            return True
        return False

class CustomImagePackage(Package):
    # CustomImageRecipe fields to track pacakges appended,
    # included and excluded from a CustomImageRecipe
    recipe_includes = models.ManyToManyField('CustomImageRecipe',
                                             related_name='includes_set')
    recipe_excludes = models.ManyToManyField('CustomImageRecipe',
                                             related_name='excludes_set')
    recipe_appends = models.ManyToManyField('CustomImageRecipe',
                                            related_name='appends_set')



class Package_DependencyManager(models.Manager):
    use_for_related_fields = True

    def get_queryset(self):
        return super(Package_DependencyManager, self).get_queryset().exclude(package_id = F('depends_on__id'))

    def get_total_source_deps_size(self):
        """ Returns the total file size of all the packages that depend on
        thispackage.
        """
        return self.all().aggregate(Sum('depends_on__size'))

    def get_total_revdeps_size(self):
        """ Returns the total file size of all the packages that depend on
        this package.
        """
        return self.all().aggregate(Sum('package_id__size'))


    def all_depends(self):
        """ Returns just the depends packages and not any other dep_type """
        return self.filter(Q(dep_type=Package_Dependency.TYPE_RDEPENDS) |
                           Q(dep_type=Package_Dependency.TYPE_TRDEPENDS))

class Package_Dependency(models.Model):
    TYPE_RDEPENDS = 0
    TYPE_TRDEPENDS = 1
    TYPE_RRECOMMENDS = 2
    TYPE_TRECOMMENDS = 3
    TYPE_RSUGGESTS = 4
    TYPE_RPROVIDES = 5
    TYPE_RREPLACES = 6
    TYPE_RCONFLICTS = 7
    ' TODO: bpackage should be changed to remove the DEPENDS_TYPE access '
    DEPENDS_TYPE = (
        (TYPE_RDEPENDS, "depends"),
        (TYPE_TRDEPENDS, "depends"),
        (TYPE_TRECOMMENDS, "recommends"),
        (TYPE_RRECOMMENDS, "recommends"),
        (TYPE_RSUGGESTS, "suggests"),
        (TYPE_RPROVIDES, "provides"),
        (TYPE_RREPLACES, "replaces"),
        (TYPE_RCONFLICTS, "conflicts"),
    )
    """ Indexed by dep_type, in view order, key for short name and help
        description which when viewed will be printf'd with the
        package name.
    """
    DEPENDS_DICT = {
        TYPE_RDEPENDS :     ("depends", "%s is required to run %s"),
        TYPE_TRDEPENDS :    ("depends", "%s is required to run %s"),
        TYPE_TRECOMMENDS :  ("recommends", "%s extends the usability of %s"),
        TYPE_RRECOMMENDS :  ("recommends", "%s extends the usability of %s"),
        TYPE_RSUGGESTS :    ("suggests", "%s is suggested for installation with %s"),
        TYPE_RPROVIDES :    ("provides", "%s is provided by %s"),
        TYPE_RREPLACES :    ("replaces", "%s is replaced by %s"),
        TYPE_RCONFLICTS :   ("conflicts", "%s conflicts with %s, which will not be installed if this package is not first removed"),
    }

    package = models.ForeignKey(Package, related_name='package_dependencies_source')
    depends_on = models.ForeignKey(Package, related_name='package_dependencies_target')   # soft dependency
    dep_type = models.IntegerField(choices=DEPENDS_TYPE)
    target = models.ForeignKey(Target, null=True)
    objects = Package_DependencyManager()

class Target_Installed_Package(models.Model):
    target = models.ForeignKey(Target)
    package = models.ForeignKey(Package, related_name='buildtargetlist_package')

class Package_File(models.Model):
    package = models.ForeignKey(Package, related_name='buildfilelist_package')
    path = models.FilePathField(max_length=255, blank=True)
    size = models.IntegerField()

class Recipe(models.Model):
    search_allowed_fields = ['name', 'version', 'file_path', 'section', 'summary', 'description', 'license', 'layer_version__layer__name', 'layer_version__branch', 'layer_version__commit', 'layer_version__local_path', 'layer_version__layer_source__name']

    layer_source = models.ForeignKey('LayerSource', default = None, null = True)  # from where did we get this recipe
    up_id = models.IntegerField(null = True, default = None)                    # id of entry in the source
    up_date = models.DateTimeField(null = True, default = None)

    name = models.CharField(max_length=100, blank=True)                 # pn
    version = models.CharField(max_length=100, blank=True)              # pv
    layer_version = models.ForeignKey('Layer_Version', related_name='recipe_layer_version')
    summary = models.TextField(blank=True)
    description = models.TextField(blank=True)
    section = models.CharField(max_length=100, blank=True)
    license = models.CharField(max_length=200, blank=True)
    homepage = models.URLField(blank=True)
    bugtracker = models.URLField(blank=True)
    file_path = models.FilePathField(max_length=255)
    pathflags = models.CharField(max_length=200, blank=True)
    is_image = models.BooleanField(default=False)

    def get_layersource_view_url(self):
        if self.layer_source is None:
            return ""

        url = self.layer_source.get_object_view(self.layer_version.up_branch, "recipes", self.name)
        return url

    def __unicode__(self):
        return "Recipe " + self.name + ":" + self.version

    def get_vcs_recipe_file_link_url(self):
        return self.layer_version.get_vcs_file_link_url(self.file_path)

    def get_description_or_summary(self):
        if self.description:
            return self.description
        elif self.summary:
            return self.summary
        else:
            return ""

    class Meta:
        unique_together = (("layer_version", "file_path", "pathflags"), )


class Recipe_DependencyManager(models.Manager):
    use_for_related_fields = True

    def get_queryset(self):
        return super(Recipe_DependencyManager, self).get_queryset().exclude(recipe_id = F('depends_on__id'))

class Provides(models.Model):
    name = models.CharField(max_length=100)
    recipe = models.ForeignKey(Recipe)

class Recipe_Dependency(models.Model):
    TYPE_DEPENDS = 0
    TYPE_RDEPENDS = 1

    DEPENDS_TYPE = (
        (TYPE_DEPENDS, "depends"),
        (TYPE_RDEPENDS, "rdepends"),
    )
    recipe = models.ForeignKey(Recipe, related_name='r_dependencies_recipe')
    depends_on = models.ForeignKey(Recipe, related_name='r_dependencies_depends')
    via = models.ForeignKey(Provides, null=True, default=None)
    dep_type = models.IntegerField(choices=DEPENDS_TYPE)
    objects = Recipe_DependencyManager()


class Machine(models.Model):
    search_allowed_fields = ["name", "description", "layer_version__layer__name"]
    layer_source = models.ForeignKey('LayerSource', default = None, null = True)  # from where did we get this machine
    up_id = models.IntegerField(null = True, default = None)                      # id of entry in the source
    up_date = models.DateTimeField(null = True, default = None)

    layer_version = models.ForeignKey('Layer_Version')
    name = models.CharField(max_length=255)
    description = models.CharField(max_length=255)

    def get_vcs_machine_file_link_url(self):
        path = 'conf/machine/'+self.name+'.conf'

        return self.layer_version.get_vcs_file_link_url(path)

    def __unicode__(self):
        return "Machine " + self.name + "(" + self.description + ")"

    class Meta:
        unique_together = ("layer_source", "up_id")


from django.db.models.base import ModelBase

class InheritanceMetaclass(ModelBase):
    def __call__(cls, *args, **kwargs):
        obj = super(InheritanceMetaclass, cls).__call__(*args, **kwargs)
        return obj.get_object()


class LayerSource(models.Model):
    __metaclass__ = InheritanceMetaclass

    class Meta:
        unique_together = (('sourcetype', 'apiurl'), )

    TYPE_LOCAL = 0
    TYPE_LAYERINDEX = 1
    TYPE_IMPORTED = 2
    SOURCE_TYPE = (
        (TYPE_LOCAL, "local"),
        (TYPE_LAYERINDEX, "layerindex"),
        (TYPE_IMPORTED, "imported"),
      )

    name = models.CharField(max_length=63, unique = True)
    sourcetype = models.IntegerField(choices=SOURCE_TYPE)
    apiurl = models.CharField(max_length=255, null=True, default=None)

    def __init__(self, *args, **kwargs):
        super(LayerSource, self).__init__(*args, **kwargs)
        if self.sourcetype == LayerSource.TYPE_LOCAL:
            self.__class__ = LocalLayerSource
        elif self.sourcetype == LayerSource.TYPE_LAYERINDEX:
            self.__class__ = LayerIndexLayerSource
        elif self.sourcetype == LayerSource.TYPE_IMPORTED:
            self.__class__ = ImportedLayerSource
        elif self.sourcetype == None:
            raise Exception("Unknown LayerSource-derived class. If you added a new layer source type, fill out all code stubs.")


    def update(self):
        """
            Updates the local database information from the upstream layer source
        """
        raise Exception("Abstract, update() must be implemented by all LayerSource-derived classes (object is %s)" % str(vars(self)))

    def save(self, *args, **kwargs):
        return super(LayerSource, self).save(*args, **kwargs)

    def get_object(self):
        # preset an un-initilized object
        if None == self.name:
            self.name=""
        if None == self.apiurl:
            self.apiurl=""
        if None == self.sourcetype:
            self.sourcetype=LayerSource.TYPE_LOCAL

        if self.sourcetype == LayerSource.TYPE_LOCAL:
            self.__class__ = LocalLayerSource
        elif self.sourcetype == LayerSource.TYPE_LAYERINDEX:
            self.__class__ = LayerIndexLayerSource
        elif self.sourcetype == LayerSource.TYPE_IMPORTED:
            self.__class__ = ImportedLayerSource
        else:
            raise Exception("Unknown LayerSource type. If you added a new layer source type, fill out all code stubs.")
        return self

    def __unicode__(self):
        return "%s (%s)" % (self.name, self.sourcetype)


class LocalLayerSource(LayerSource):
    class Meta(LayerSource._meta.__class__):
        proxy = True

    def __init__(self, *args, **kwargs):
        super(LocalLayerSource, self).__init__(args, kwargs)
        self.sourcetype = LayerSource.TYPE_LOCAL

    def update(self):
        """
            Fetches layer, recipe and machine information from local repository
        """
        pass

class ImportedLayerSource(LayerSource):
    class Meta(LayerSource._meta.__class__):
        proxy = True

    def __init__(self, *args, **kwargs):
        super(ImportedLayerSource, self).__init__(args, kwargs)
        self.sourcetype = LayerSource.TYPE_IMPORTED

    def update(self):
        """
            Fetches layer, recipe and machine information from local repository
        """
        pass


class LayerIndexLayerSource(LayerSource):
    class Meta(LayerSource._meta.__class__):
        proxy = True

    def __init__(self, *args, **kwargs):
        super(LayerIndexLayerSource, self).__init__(args, kwargs)
        self.sourcetype = LayerSource.TYPE_LAYERINDEX

    def get_object_view(self, branch, objectype, upid):
        return self.apiurl + "../branch/" + branch.name + "/" + objectype + "/?q=" + str(upid)

    def update(self):
        """
            Fetches layer, recipe and machine information from remote repository
        """
        assert self.apiurl is not None
        from django.db import transaction, connection

        import urllib2, urlparse, json
        import os
        proxy_settings = os.environ.get("http_proxy", None)
        oe_core_layer = 'openembedded-core'

        def _get_json_response(apiurl = self.apiurl):
            _parsedurl = urlparse.urlparse(apiurl)
            path = _parsedurl.path

            try:
                res = urllib2.urlopen(apiurl)
            except urllib2.URLError as e:
                raise Exception("Failed to read %s: %s" % (path, e.reason))

            return json.loads(res.read())

        # verify we can get the basic api
        try:
            apilinks = _get_json_response()
        except Exception as e:
            import traceback
            if proxy_settings is not None:
                logger.info("EE: Using proxy %s" % proxy_settings)
            logger.warning("EE: could not connect to %s, skipping update: %s\n%s" % (self.apiurl, e, traceback.format_exc(e)))
            return

        # update branches; only those that we already have names listed in the
        # Releases table
        whitelist_branch_names = map(lambda x: x.branch_name, Release.objects.all())
        if len(whitelist_branch_names) == 0:
            raise Exception("Failed to make list of branches to fetch")

        logger.debug("Fetching branches")
        branches_info = _get_json_response(apilinks['branches']
            + "?filter=name:%s" % "OR".join(whitelist_branch_names))
        for bi in branches_info:
            b, created = Branch.objects.get_or_create(layer_source = self, name = bi['name'])
            b.up_id = bi['id']
            b.up_date = bi['updated']
            b.name = bi['name']
            b.short_description = bi['short_description']
            b.save()

        # update layers
        layers_info = _get_json_response(apilinks['layerItems'])

        for li in layers_info:
            # Special case for the openembedded-core layer
            if li['name'] == oe_core_layer:
                try:
                    # If we have an existing openembedded-core for example
                    # from the toasterconf.json augment the info using the
                    # layerindex rather than duplicate it
                    oe_core_l =  Layer.objects.get(name=oe_core_layer)
                    # Take ownership of the layer as now coming from the
                    # layerindex
                    oe_core_l.layer_source = self
                    oe_core_l.up_id = li['id']
                    oe_core_l.summary = li['summary']
                    oe_core_l.description = li['description']
                    oe_core_l.save()
                    continue

                except Layer.DoesNotExist:
                    pass

            l, created = Layer.objects.get_or_create(layer_source = self, name = li['name'])
            l.up_id = li['id']
            l.up_date = li['updated']
            l.vcs_url = li['vcs_url']
            l.vcs_web_url = li['vcs_web_url']
            l.vcs_web_tree_base_url = li['vcs_web_tree_base_url']
            l.vcs_web_file_base_url = li['vcs_web_file_base_url']
            l.summary = li['summary']
            l.description = li['description']
            l.save()

        # update layerbranches/layer_versions
        logger.debug("Fetching layer information")
        layerbranches_info = _get_json_response(apilinks['layerBranches']
                + "?filter=branch:%s" % "OR".join(map(lambda x: str(x.up_id), [i for i in Branch.objects.filter(layer_source = self) if i.up_id is not None] ))
            )

        for lbi in layerbranches_info:
            lv, created = Layer_Version.objects.get_or_create(layer_source = self,
                    up_id = lbi['id'],
                    layer=Layer.objects.get(layer_source = self, up_id = lbi['layer'])
                )

            lv.up_date = lbi['updated']
            lv.up_branch = Branch.objects.get(layer_source = self, up_id = lbi['branch'])
            lv.branch = lbi['actual_branch']
            lv.commit = lbi['actual_branch']
            lv.dirpath = lbi['vcs_subdir']
            lv.save()

        # update layer dependencies
        layerdependencies_info = _get_json_response(apilinks['layerDependencies'])
        dependlist = {}
        for ldi in layerdependencies_info:
            try:
                lv = Layer_Version.objects.get(layer_source = self, up_id = ldi['layerbranch'])
            except Layer_Version.DoesNotExist as e:
                continue

            if lv not in dependlist:
                dependlist[lv] = []
            try:
                dependlist[lv].append(Layer_Version.objects.get(layer_source = self, layer__up_id = ldi['dependency'], up_branch = lv.up_branch))
            except Layer_Version.DoesNotExist:
                logger.warning("Cannot find layer version (ls:%s), up_id:%s lv:%s" % (self, ldi['dependency'], lv))

        for lv in dependlist:
            LayerVersionDependency.objects.filter(layer_version = lv).delete()
            for lvd in dependlist[lv]:
                LayerVersionDependency.objects.get_or_create(layer_version = lv, depends_on = lvd)


        # update machines
        logger.debug("Fetching machine information")
        machines_info = _get_json_response(apilinks['machines']
                + "?filter=layerbranch:%s" % "OR".join(map(lambda x: str(x.up_id), Layer_Version.objects.filter(layer_source = self)))
            )

        for mi in machines_info:
            mo, created = Machine.objects.get_or_create(layer_source = self, up_id = mi['id'], layer_version = Layer_Version.objects.get(layer_source = self, up_id = mi['layerbranch']))
            mo.up_date = mi['updated']
            mo.name = mi['name']
            mo.description = mi['description']
            mo.save()

        # update recipes; paginate by layer version / layer branch
        logger.debug("Fetching target information")
        recipes_info = _get_json_response(apilinks['recipes']
                + "?filter=layerbranch:%s" % "OR".join(map(lambda x: str(x.up_id), Layer_Version.objects.filter(layer_source = self)))
            )
        for ri in recipes_info:
            try:
                ro, created = Recipe.objects.get_or_create(layer_source = self, up_id = ri['id'], layer_version = Layer_Version.objects.get(layer_source = self, up_id = ri['layerbranch']))
                ro.up_date = ri['updated']
                ro.name = ri['pn']
                ro.version = ri['pv']
                ro.summary = ri['summary']
                ro.description = ri['description']
                ro.section = ri['section']
                ro.license = ri['license']
                ro.homepage = ri['homepage']
                ro.bugtracker = ri['bugtracker']
                ro.file_path = ri['filepath'] + "/" + ri['filename']
                if 'inherits' in ri:
                    ro.is_image = 'image' in ri['inherits'].split()
                else: # workaround for old style layer index
                    ro.is_image = "-image-" in ri['pn']
                ro.save()
            except IntegrityError as e:
                logger.debug("Failed saving recipe, ignoring: %s (%s:%s)" % (e, ro.layer_version, ri['filepath']+"/"+ri['filename']))
                ro.delete()

class BitbakeVersion(models.Model):

    name = models.CharField(max_length=32, unique = True)
    giturl = GitURLField()
    branch = models.CharField(max_length=32)
    dirpath = models.CharField(max_length=255)

    def __unicode__(self):
        return "%s (Branch: %s)" % (self.name, self.branch)


class Release(models.Model):
    """ A release is a project template, used to pre-populate Project settings with a configuration set """
    name = models.CharField(max_length=32, unique = True)
    description = models.CharField(max_length=255)
    bitbake_version = models.ForeignKey(BitbakeVersion)
    branch_name = models.CharField(max_length=50, default = "")
    helptext = models.TextField(null=True)

    def __unicode__(self):
        return "%s (%s)" % (self.name, self.branch_name)

class ReleaseLayerSourcePriority(models.Model):
    """ Each release selects layers from the set up layer sources, ordered by priority """
    release = models.ForeignKey("Release")
    layer_source = models.ForeignKey("LayerSource")
    priority = models.IntegerField(default = 0)

    def __unicode__(self):
        return "%s-%s:%d" % (self.release.name, self.layer_source.name, self.priority)
    class Meta:
        unique_together = (('release', 'layer_source'),)


class ReleaseDefaultLayer(models.Model):
    release = models.ForeignKey(Release)
    layer_name = models.CharField(max_length=100, default="")


# Branch class is synced with layerindex.Branch, branches can only come from remote layer indexes
class Branch(models.Model):
    layer_source = models.ForeignKey('LayerSource', null = True, default = True)
    up_id = models.IntegerField(null = True, default = None)                    # id of branch in the source
    up_date = models.DateTimeField(null = True, default = None)

    name = models.CharField(max_length=50)
    short_description = models.CharField(max_length=50, blank=True)

    class Meta:
        verbose_name_plural = "Branches"
        unique_together = (('layer_source', 'name'),('layer_source', 'up_id'))

    def __unicode__(self):
        return self.name


# Layer class synced with layerindex.LayerItem
class Layer(models.Model):
    layer_source = models.ForeignKey(LayerSource, null = True, default = None)  # from where did we got this layer
    up_id = models.IntegerField(null = True, default = None)                    # id of layer in the remote source
    up_date = models.DateTimeField(null = True, default = None)

    name = models.CharField(max_length=100)
    layer_index_url = models.URLField()
    vcs_url = GitURLField(default = None, null = True)
    vcs_web_url = models.URLField(null = True, default = None)
    vcs_web_tree_base_url = models.URLField(null = True, default = None)
    vcs_web_file_base_url = models.URLField(null = True, default = None)

    summary = models.TextField(help_text='One-line description of the layer', null = True, default = None)
    description = models.TextField(null = True, default = None)

    def __unicode__(self):
        return "%s / %s " % (self.name, self.layer_source)

    class Meta:
        unique_together = (("layer_source", "up_id"), ("layer_source", "name"))


# LayerCommit class is synced with layerindex.LayerBranch
class Layer_Version(models.Model):
    """
    A Layer_Version either belongs to a single project or no project
    """
    search_allowed_fields = ["layer__name", "layer__summary", "layer__description", "layer__vcs_url", "dirpath", "up_branch__name", "commit", "branch"]
    build = models.ForeignKey(Build, related_name='layer_version_build', default = None, null = True)
    layer = models.ForeignKey(Layer, related_name='layer_version_layer')

    layer_source = models.ForeignKey(LayerSource, null = True, default = None)                   # from where did we get this Layer Version
    up_id = models.IntegerField(null = True, default = None)        # id of layerbranch in the remote source
    up_date = models.DateTimeField(null = True, default = None)
    up_branch = models.ForeignKey(Branch, null = True, default = None)

    branch = models.CharField(max_length=80)            # LayerBranch.actual_branch
    commit = models.CharField(max_length=100)           # LayerBranch.vcs_last_rev
    dirpath = models.CharField(max_length=255, null = True, default = None)          # LayerBranch.vcs_subdir
    priority = models.IntegerField(default = 0)         # if -1, this is a default layer

    local_path = models.FilePathField(max_length=1024, default = "/")  # where this layer was checked-out

    project = models.ForeignKey('Project', null = True, default = None)   # Set if this layer is project-specific; always set for imported layers, and project-set branches

    # code lifted, with adaptations, from the layerindex-web application https://git.yoctoproject.org/cgit/cgit.cgi/layerindex-web/
    def _handle_url_path(self, base_url, path):
        import re, posixpath
        if base_url:
            if self.dirpath:
                if path:
                    extra_path = self.dirpath + '/' + path
                    # Normalise out ../ in path for usage URL
                    extra_path = posixpath.normpath(extra_path)
                    # Minor workaround to handle case where subdirectory has been added between branches
                    # (should probably support usage URL per branch to handle this... sigh...)
                    if extra_path.startswith('../'):
                        extra_path = extra_path[3:]
                else:
                    extra_path = self.dirpath
            else:
                extra_path = path
            branchname = self.up_branch.name
            url = base_url.replace('%branch%', branchname)

            # If there's a % in the path (e.g. a wildcard bbappend) we need to encode it
            if extra_path:
                extra_path = extra_path.replace('%', '%25')

            if '%path%' in base_url:
                if extra_path:
                    url = re.sub(r'\[([^\]]*%path%[^\]]*)\]', '\\1', url)
                else:
                    url = re.sub(r'\[([^\]]*%path%[^\]]*)\]', '', url)
                return url.replace('%path%', extra_path)
            else:
                return url + extra_path
        return None

    def get_vcs_link_url(self):
        if self.layer.vcs_web_url is None:
            return None
        return self.layer.vcs_web_url

    def get_vcs_file_link_url(self, file_path=""):
        if self.layer.vcs_web_file_base_url is None:
            return None
        return self._handle_url_path(self.layer.vcs_web_file_base_url, file_path)

    def get_vcs_dirpath_link_url(self):
        if self.layer.vcs_web_tree_base_url is None:
            return None
        return self._handle_url_path(self.layer.vcs_web_tree_base_url, '')

    def get_equivalents_wpriority(self, project):
        layer_versions = project.get_all_compatible_layer_versions()
        filtered = layer_versions.filter(layer__name = self.layer.name)
        return filtered.order_by("-layer_source__releaselayersourcepriority__priority")

    def get_vcs_reference(self):
        if self.branch is not None and len(self.branch) > 0:
            return self.branch
        if self.up_branch is not None:
            return self.up_branch.name
        if self.commit is not None and len(self.commit) > 0:
            return self.commit
        return 'N/A'

    def get_detailspage_url(self, project_id):
        return reverse('layerdetails', args=(project_id, self.pk))

    def get_alldeps(self, project_id):
        """Get full list of unique layer dependencies."""
        def gen_layerdeps(lver, project):
            for ldep in lver.dependencies.all():
                yield ldep.depends_on
                # get next level of deps recursively calling gen_layerdeps
                for subdep in gen_layerdeps(ldep.depends_on, project):
                    yield subdep

        project = Project.objects.get(pk=project_id)
        result = []
        projectlvers = [player.layercommit for player in project.projectlayer_set.all()]
        for dep in gen_layerdeps(self, project):
            # filter out duplicates and layers already belonging to the project
            if dep not in result + projectlvers:
                result.append(dep)

        return sorted(result, key=lambda x: x.layer.name)

    def __unicode__(self):
        return "%d %s (VCS %s, Project %s)" % (self.pk, str(self.layer), self.get_vcs_reference(), self.build.project if self.build is not None else "No project")

    class Meta:
        unique_together = ("layer_source", "up_id")

class LayerVersionDependency(models.Model):
    layer_source = models.ForeignKey(LayerSource, null = True, default = None)  # from where did we got this layer
    up_id = models.IntegerField(null = True, default = None)                    # id of layerbranch in the remote source

    layer_version = models.ForeignKey(Layer_Version, related_name="dependencies")
    depends_on = models.ForeignKey(Layer_Version, related_name="dependees")

    class Meta:
        unique_together = ("layer_source", "up_id")

class ProjectLayer(models.Model):
    project = models.ForeignKey(Project)
    layercommit = models.ForeignKey(Layer_Version, null=True)
    optional = models.BooleanField(default = True)

    def __unicode__(self):
        return "%s, %s" % (self.project.name, self.layercommit)

    class Meta:
        unique_together = (("project", "layercommit"),)

class CustomImageRecipe(Recipe):

    # CustomImageRecipe's belong to layers called:
    LAYER_NAME = "toaster-custom-images"

    search_allowed_fields = ['name']
    base_recipe = models.ForeignKey(Recipe, related_name='based_on_recipe')
    project = models.ForeignKey(Project)
    last_updated = models.DateTimeField(null=True, default=None)

    def get_last_successful_built_target(self):
        """ Return the last successful built target object if one exists
        otherwise return None """
        return Target.objects.filter(Q(build__outcome=Build.SUCCEEDED) &
                                     Q(build__project=self.project) &
                                     Q(target=self.name)).last()

    def update_package_list(self):
        """ Update the package list from the last good build of this
        CustomImageRecipe
        """
        # Check if we're aldready up-to-date or not
        target = self.get_last_successful_built_target()
        if target == None:
            # So we've never actually built this Custom recipe but what about
            # the recipe it's based on?
            target = \
                Target.objects.filter(Q(build__outcome=Build.SUCCEEDED) &
                                      Q(build__project=self.project) &
                                      Q(target=self.base_recipe.name)).last()
            if target == None:
                return

        if target.build.completed_on == self.last_updated:
            return

        self.includes_set.clear()

        excludes_list = self.excludes_set.values_list('name', flat=True)
        appends_list = self.appends_set.values_list('name', flat=True)

        built_packages_list = \
            target.target_installed_package_set.values_list('package__name',
                                                            flat=True)
        for built_package in built_packages_list:
            # Is the built package in the custom packages list?
            if built_package in excludes_list:
                continue

            if built_package in appends_list:
                continue

            cust_img_p = \
                    CustomImagePackage.objects.get(name=built_package)
            self.includes_set.add(cust_img_p)


        self.last_updated = target.build.completed_on
        self.save()

    def get_all_packages(self):
        """Get the included packages and any appended packages"""
        self.update_package_list()

        return CustomImagePackage.objects.filter((Q(recipe_appends=self) |
                                                  Q(recipe_includes=self)) &
                                                 ~Q(recipe_excludes=self))


    def generate_recipe_file_contents(self):
        """Generate the contents for the recipe file."""
        # If we have no excluded packages we only need to _append
        if self.excludes_set.count() == 0:
            packages_conf = "IMAGE_INSTALL_append = \" "

            for pkg in self.appends_set.all():
                packages_conf += pkg.name+' '
        else:
            packages_conf = "IMAGE_FEATURES =\"\"\nIMAGE_INSTALL = \""
            # We add all the known packages to be built by this recipe apart
            # from locale packages which are are controlled with IMAGE_LINGUAS.
            for pkg in self.get_all_packages().exclude(
                name__icontains="locale"):
                packages_conf += pkg.name+' '

        packages_conf += "\""
        try:
            base_recipe = open("%s/%s" %
                               (self.base_recipe.layer_version.dirpath,
                                self.base_recipe.file_path), 'r').read()
        except IOError:
            # The path may now be the full path if the recipe has been built
            base_recipe = open(self.base_recipe.file_path, 'r').read()

        # Add a special case for when the recipe we have based a custom image
        # recipe on requires another recipe.
        # For example:
        # "require core-image-minimal.bb" is changed to:
        # "require recipes-core/images/core-image-minimal.bb"

        req_search = re.search(r'(require\s+)(.+\.bb\s*$)',
                                   base_recipe,
                                   re.MULTILINE)
        if req_search:
            require_filename = req_search.group(2).strip()

            corrected_location = Recipe.objects.filter(
                Q(layer_version=self.base_recipe.layer_version) &
                Q(file_path__icontains=require_filename)).last().file_path

            new_require_line = "require %s" % corrected_location

            base_recipe = \
                    base_recipe.replace(req_search.group(0), new_require_line)


        info = {"date" : timezone.now().strftime("%Y-%m-%d %H:%M:%S"),
                "base_recipe" : base_recipe,
                "recipe_name" : self.name,
                "base_recipe_name" : self.base_recipe.name,
                "license" : self.license,
                "summary" : self.summary,
                "description" : self.description,
                "packages_conf" : packages_conf.strip(),
               }

        recipe_contents = ("# Original recipe %(base_recipe_name)s \n"
                           "%(base_recipe)s\n\n"
                           "# Recipe %(recipe_name)s \n"
                           "# Customisation Generated by Toaster on %(date)s\n"
                           "SUMMARY = \"%(summary)s\"\n"
                           "DESCRIPTION = \"%(description)s\"\n"
                           "LICENSE = \"%(license)s\"\n"
                           "%(packages_conf)s") % info

        return recipe_contents

class ProjectVariable(models.Model):
    project = models.ForeignKey(Project)
    name = models.CharField(max_length=100)
    value = models.TextField(blank = True)

class Variable(models.Model):
    search_allowed_fields = ['variable_name', 'variable_value',
                             'vhistory__file_name', "description"]
    build = models.ForeignKey(Build, related_name='variable_build')
    variable_name = models.CharField(max_length=100)
    variable_value = models.TextField(blank=True)
    changed = models.BooleanField(default=False)
    human_readable_name = models.CharField(max_length=200)
    description = models.TextField(blank=True)

class VariableHistory(models.Model):
    variable = models.ForeignKey(Variable, related_name='vhistory')
    value   = models.TextField(blank=True)
    file_name = models.FilePathField(max_length=255)
    line_number = models.IntegerField(null=True)
    operation = models.CharField(max_length=64)

class HelpText(models.Model):
    VARIABLE = 0
    HELPTEXT_AREA = ((VARIABLE, 'variable'), )

    build = models.ForeignKey(Build, related_name='helptext_build')
    area = models.IntegerField(choices=HELPTEXT_AREA)
    key = models.CharField(max_length=100)
    text = models.TextField()

class LogMessage(models.Model):
    EXCEPTION = -1      # used to signal self-toaster-exceptions
    INFO = 0
    WARNING = 1
    ERROR = 2
    CRITICAL = 3

    LOG_LEVEL = (
        (INFO, "info"),
        (WARNING, "warn"),
        (ERROR, "error"),
        (CRITICAL, "critical"),
        (EXCEPTION, "toaster exception")
    )

    build = models.ForeignKey(Build)
    task  = models.ForeignKey(Task, blank = True, null=True)
    level = models.IntegerField(choices=LOG_LEVEL, default=INFO)
    message = models.TextField(blank=True, null=True)
    pathname = models.FilePathField(max_length=255, blank=True)
    lineno = models.IntegerField(null=True)

    def __str__(self):
        return force_bytes('%s %s %s' % (self.get_level_display(), self.message, self.build))

def invalidate_cache(**kwargs):
    from django.core.cache import cache
    try:
      cache.clear()
    except Exception as e:
      logger.warning("Problem with cache backend: Failed to clear cache: %s" % e)

django.db.models.signals.post_save.connect(invalidate_cache)
django.db.models.signals.post_delete.connect(invalidate_cache)
django.db.models.signals.m2m_changed.connect(invalidate_cache)
