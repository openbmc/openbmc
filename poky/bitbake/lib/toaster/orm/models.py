#
# BitBake Toaster Implementation
#
# Copyright (C) 2013        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from __future__ import unicode_literals

from django.db import models, IntegrityError, DataError
from django.db.models import F, Q, Sum, Count
from django.utils import timezone
from django.utils.encoding import force_bytes

from django.urls import reverse

from django.core import validators
from django.conf import settings
import django.db.models.signals

import sys
import os
import re
import itertools
from signal import SIGUSR1


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

    def _create_object_from_params(self, lookup, params):
        """
        Tries to create an object using passed params.
        Used by get_or_create and update_or_create
        """
        try:
            obj = self.create(**params)
            return obj, True
        except (IntegrityError, DataError):
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
    for i in range(len(r.validators)):
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
    def create_project(self, name, release, existing_project=None, imported=False):
        if existing_project and (release is not None):
            prj = existing_project
            prj.bitbake_version = release.bitbake_version
            prj.release = release
            # Delete the previous ProjectLayer mappings
            for pl in ProjectLayer.objects.filter(project=prj):
                pl.delete()
        elif release is not None:
            prj = self.model(name=name,
                             bitbake_version=release.bitbake_version,
                             release=release)
        else:
            prj = self.model(name=name,
                             bitbake_version=None,
                             release=None)
        prj.save()

        for defaultconf in ToasterSetting.objects.filter(
                name__startswith="DEFCONF_"):
            name = defaultconf.name[8:]
            pv,create = ProjectVariable.objects.get_or_create(project=prj,name=name)
            pv.value = defaultconf.value
            pv.save()

        if release is None:
            return prj
        if not imported:
            for rdl in release.releasedefaultlayer_set.all():
                lv = Layer_Version.objects.filter(
                    layer__name=rdl.layer_name,
                    release=release).first()

                if lv:
                    ProjectLayer.objects.create(project=prj,
                                                layercommit=lv,
                                                optional=False)
                else:
                    logger.warning("Default project layer %s not found" %
                                rdl.layer_name)

        return prj

    # return single object with is_default = True
    def get_or_create_default_project(self):
        projects = super(ProjectManager, self).filter(is_default=True)

        if len(projects) > 1:
            raise Exception('Inconsistent project data: multiple ' +
                            'default projects (i.e. with is_default=True)')
        elif len(projects) < 1:
            options = {
                'name': 'Command line builds',
                'short_description':
                'Project for builds started outside Toaster',
                'is_default': True
            }
            project = Project.objects.create(**options)
            project.save()

            return project
        else:
            return projects[0]


class Project(models.Model):
    search_allowed_fields = ['name', 'short_description', 'release__name',
                             'release__branch_name']
    name = models.CharField(max_length=100)
    short_description = models.CharField(max_length=50, blank=True)
    bitbake_version = models.ForeignKey('BitbakeVersion', on_delete=models.CASCADE, null=True)
    release = models.ForeignKey("Release", on_delete=models.CASCADE, null=True)
    created = models.DateTimeField(auto_now_add=True)
    updated = models.DateTimeField(auto_now=True)
    # This is a horrible hack; since Toaster has no "User" model available when
    # running in interactive mode, we can't reference the field here directly
    # Instead, we keep a possible null reference to the User id,
    # as not to force
    # hard links to possibly missing models
    user_id = models.IntegerField(null=True)
    objects = ProjectManager()

    # build directory override (e.g. imported)
    builddir = models.TextField()
    # merge the Toaster configure attributes directly into the standard conf files
    merged_attr = models.BooleanField(default=False)

    # set to True for the project which is the default container
    # for builds initiated by the command line etc.
    is_default= models.BooleanField(default=False)

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
        build_id = self.get_last_build_id()
        if (-1 == build_id):
            return( "" )
        try:
            return Build.objects.filter( id = build_id )[ 0 ].outcome
        except (Build.DoesNotExist,IndexError):
            return( "not_found" )

    def get_last_target(self):
        build_id = self.get_last_build_id()
        if (-1 == build_id):
            return( "" )
        try:
            return Target.objects.filter(build = build_id)[0].target
        except (Target.DoesNotExist,IndexError):
            return( "not_found" )

    def get_last_errors(self):
        build_id = self.get_last_build_id()
        if (-1 == build_id):
            return( 0 )
        try:
            return Build.objects.filter(id = build_id)[ 0 ].errors.count()
        except (Build.DoesNotExist,IndexError):
            return( "not_found" )

    def get_last_warnings(self):
        build_id = self.get_last_build_id()
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
        build_id = self.get_last_build_id()
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
                (Q(release=self.release) &
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


    def get_default_image_recipe(self):
        try:
            return self.projectvariable_set.get(name="DEFAULT_IMAGE").value
        except (ProjectVariable.DoesNotExist,IndexError):
            return None;

    def get_is_new(self):
        return self.get_variable(Project.PROJECT_SPECIFIC_ISNEW)

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

    def get_available_distros(self):
        """ Returns QuerySet of all Distros which are provided by the
        Layers currently added to the Project """
        queryset = Distro.objects.filter(
            layer_version__in=self.get_project_layer_versions())

        return queryset

    def get_all_compatible_distros(self):
        """ Returns QuerySet of all the compatible Wind River distros available to the
        project including ones from Layers not currently added """
        queryset = Distro.objects.filter(
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

    # Project Specific status management
    PROJECT_SPECIFIC_STATUS = 'INTERNAL_PROJECT_SPECIFIC_STATUS'
    PROJECT_SPECIFIC_CALLBACK = 'INTERNAL_PROJECT_SPECIFIC_CALLBACK'
    PROJECT_SPECIFIC_ISNEW = 'INTERNAL_PROJECT_SPECIFIC_ISNEW'
    PROJECT_SPECIFIC_DEFAULTIMAGE = 'PROJECT_SPECIFIC_DEFAULTIMAGE'
    PROJECT_SPECIFIC_NONE = ''
    PROJECT_SPECIFIC_NEW = '1'
    PROJECT_SPECIFIC_EDIT = '2'
    PROJECT_SPECIFIC_CLONING = '3'
    PROJECT_SPECIFIC_CLONING_SUCCESS = '4'
    PROJECT_SPECIFIC_CLONING_FAIL = '5'

    def get_variable(self,variable,default_value = ''):
        try:
            return self.projectvariable_set.get(name=variable).value
        except (ProjectVariable.DoesNotExist,IndexError):
            return default_value

    def set_variable(self,variable,value):
        pv,create = ProjectVariable.objects.get_or_create(project = self, name = variable)
        pv.value = value
        pv.save()

    def get_default_image(self):
        return self.get_variable(Project.PROJECT_SPECIFIC_DEFAULTIMAGE)

    def schedule_build(self):

        from bldcontrol.models import BuildRequest, BRTarget, BRLayer
        from bldcontrol.models import BRBitbake, BRVariable

        try:
            now = timezone.now()
            build = Build.objects.create(project=self,
                                         completed_on=now,
                                         started_on=now)

            br = BuildRequest.objects.create(project=self,
                                             state=BuildRequest.REQ_QUEUED,
                                             build=build)
            BRBitbake.objects.create(req=br,
                                     giturl=self.bitbake_version.giturl,
                                     commit=self.bitbake_version.branch,
                                     dirpath=self.bitbake_version.dirpath)

            for t in self.projecttarget_set.all():
                BRTarget.objects.create(req=br, target=t.target, task=t.task)
                Target.objects.create(build=br.build, target=t.target,
                                      task=t.task)
                # If we're about to build a custom image recipe make sure
                # that layer is currently in the project before we create the
                # BRLayer objects
                customrecipe = CustomImageRecipe.objects.filter(
                    name=t.target,
                    project=self).first()
                if customrecipe:
                    ProjectLayer.objects.get_or_create(
                        project=self,
                        layercommit=customrecipe.layer_version,
                        optional=False)

            for l in self.projectlayer_set.all().order_by("pk"):
                commit = l.layercommit.get_vcs_reference()
                logger.debug("Adding layer to build %s" %
                             l.layercommit.layer.name)
                BRLayer.objects.create(
                    req=br,
                    name=l.layercommit.layer.name,
                    giturl=l.layercommit.layer.vcs_url,
                    commit=commit,
                    dirpath=l.layercommit.dirpath,
                    layer_version=l.layercommit,
                    local_source_dir=l.layercommit.layer.local_source_dir
                )

            for v in self.projectvariable_set.all():
                BRVariable.objects.create(req=br, name=v.name, value=v.value)

            try:
                br.build.machine = self.projectvariable_set.get(
                    name='MACHINE').value
                br.build.save()
            except ProjectVariable.DoesNotExist:
                pass

            br.save()
            signal_runbuilds()

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

    project = models.ForeignKey(Project, on_delete=models.CASCADE)            # must have a project
    machine = models.CharField(max_length=100)
    distro = models.CharField(max_length=100)
    distro_version = models.CharField(max_length=100)
    started_on = models.DateTimeField()
    completed_on = models.DateTimeField()
    outcome = models.IntegerField(choices=BUILD_OUTCOME, default=IN_PROGRESS)
    cooker_log_path = models.CharField(max_length=500)
    build_name = models.CharField(max_length=100, default='')
    bitbake_version = models.CharField(max_length=50)

    # number of recipes to parse for this build
    recipes_to_parse = models.IntegerField(default=1)

    # number of recipes parsed so far for this build
    recipes_parsed = models.IntegerField(default=1)

    # number of repos to clone for this build
    repos_to_clone = models.IntegerField(default=1)

    # number of repos cloned so far for this build (default off)
    repos_cloned = models.IntegerField(default=1)

    # Hint on current progress item
    progress_item = models.CharField(max_length=40)

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
            build.outcomeText = build.get_outcome_text()

        return recent_builds

    def started(self):
        """
        As build variables are only added for a build when its BuildStarted event
        is received, a build with no build variables is counted as
        "in preparation" and not properly started yet. This method
        will return False if a build has no build variables (it never properly
        started), or True otherwise.

        Note that this is a temporary workaround for the fact that we don't
        have a fine-grained state variable on a build which would allow us
        to record "in progress" (BuildStarted received) vs. "in preparation".
        """
        variables = Variable.objects.filter(build=self)
        return len(variables) > 0

    def completeper(self):
        tf = Task.objects.filter(build = self)
        tfc = tf.count()
        if tfc > 0:
            completeper = tf.exclude(outcome=Task.OUTCOME_NA).count()*100 // tfc
        else:
            completeper = 0
        return completeper

    def eta(self):
        eta = timezone.now()
        completeper = self.completeper()
        if self.completeper() > 0:
            eta += ((eta - self.started_on)*(100-completeper))/completeper
        return eta

    def has_images(self):
        """
        Returns True if at least one of the targets for this build has an
        image file associated with it, False otherwise
        """
        targets = Target.objects.filter(build_id=self.id)
        has_images = False
        for target in targets:
            if target.has_images():
                has_images = True
                break
        return has_images

    def has_image_recipes(self):
        """
        Returns True if a build has any targets which were built from
        image recipes.
        """
        image_recipes = self.get_image_recipes()
        return len(image_recipes) > 0

    def get_image_file_extensions(self):
        """
        Get string of file name extensions for images produced by this build;
        note that this is the actual list of extensions stored on Target objects
        for this build, and not the value of IMAGE_FSTYPES.

        Returns comma-separated string, e.g. "vmdk, ext4"
        """
        extensions = []

        targets = Target.objects.filter(build_id = self.id)
        for target in targets:
            if not target.is_image:
                continue

            target_image_files = Target_Image_File.objects.filter(
                target_id=target.id)

            for target_image_file in target_image_files:
                extensions.append(target_image_file.suffix)

        extensions = list(set(extensions))
        extensions.sort()

        return ', '.join(extensions)

    def get_image_fstypes(self):
        """
        Get the IMAGE_FSTYPES variable value for this build as a de-duplicated
        list of image file suffixes.
        """
        image_fstypes = Variable.objects.get(
            build=self, variable_name='IMAGE_FSTYPES').variable_value
        return list(set(re.split(r' {1,}', image_fstypes)))

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

    def get_buildrequest(self):
        buildrequest = None
        if hasattr(self, 'buildrequest'):
            buildrequest = self.buildrequest
        return buildrequest

    def is_queued(self):
        from bldcontrol.models import BuildRequest
        buildrequest = self.get_buildrequest()
        if buildrequest:
            return buildrequest.state == BuildRequest.REQ_QUEUED
        else:
            return False

    def is_cancelling(self):
        from bldcontrol.models import BuildRequest
        buildrequest = self.get_buildrequest()
        if buildrequest:
            return self.outcome == Build.IN_PROGRESS and \
                buildrequest.state == BuildRequest.REQ_CANCELLING
        else:
            return False

    def is_cloning(self):
        """
        True if the build is still cloning repos
        """
        return self.outcome == Build.IN_PROGRESS and \
            self.repos_cloned < self.repos_to_clone

    def is_parsing(self):
        """
        True if the build is still parsing recipes
        """
        return self.outcome == Build.IN_PROGRESS and \
            self.recipes_parsed < self.recipes_to_parse

    def is_starting(self):
        """
        True if the build has no completed tasks yet and is still just starting
        tasks.

        Note that the mechanism for testing whether a Task is "done" is whether
        its outcome field is set, as per the completeper() method.
        """
        return self.outcome == Build.IN_PROGRESS and \
            self.task_build.exclude(outcome=Task.OUTCOME_NA).count() == 0


    def get_state(self):
        """
        Get the state of the build; one of 'Succeeded', 'Failed', 'In Progress',
        'Cancelled' (Build outcomes); or 'Queued', 'Cancelling' (states
        dependent on the BuildRequest state).

        This works around the fact that we have BuildRequest states as well
        as Build states, but really we just want to know the state of the build.
        """
        if self.is_cancelling():
            return 'Cancelling';
        elif self.is_queued():
            return 'Queued'
        elif self.is_cloning():
            return 'Cloning'
        elif self.is_parsing():
            return 'Parsing'
        elif self.is_starting():
            return 'Starting'
        else:
            return self.get_outcome_text()

    def __str__(self):
        return "%d %s %s" % (self.id, self.project, ",".join([t.target for t in self.target_set.all()]))

class ProjectTarget(models.Model):
    project = models.ForeignKey(Project, on_delete=models.CASCADE)
    target = models.CharField(max_length=100)
    task = models.CharField(max_length=100, null=True)

class Target(models.Model):
    search_allowed_fields = ['target', 'file_name']
    build = models.ForeignKey(Build, on_delete=models.CASCADE)
    target = models.CharField(max_length=100)
    task = models.CharField(max_length=100, null=True)
    is_image = models.BooleanField(default = False)
    image_size = models.IntegerField(default=0)
    license_manifest_path = models.CharField(max_length=500, null=True)
    package_manifest_path = models.CharField(max_length=500, null=True)

    def package_count(self):
        return Target_Installed_Package.objects.filter(target_id__exact=self.id).count()

    def __unicode__(self):
        return self.target

    def get_similar_targets(self):
        """
        Get target sfor the same machine, task and target name
        (e.g. 'core-image-minimal') from a successful build for this project
        (but excluding this target).

        Note that we only look for targets built by this project because
        projects can have different configurations from each other, and put
        their artifacts in different directories.

        The possibility of error when retrieving candidate targets
        is minimised by the fact that bitbake will rebuild artifacts if MACHINE
        (or various other variables) change. In this case, there is no need to
        clone artifacts from another target, as those artifacts will have
        been re-generated for this target anyway.
        """
        query = ~Q(pk=self.pk) & \
            Q(target=self.target) & \
            Q(build__machine=self.build.machine) & \
            Q(build__outcome=Build.SUCCEEDED) & \
            Q(build__project=self.build.project)

        return Target.objects.filter(query)

    def get_similar_target_with_image_files(self):
        """
        Get the most recent similar target with Target_Image_Files associated
        with it, for the purpose of cloning those files onto this target.
        """
        similar_target = None

        candidates = self.get_similar_targets()
        if candidates.count() == 0:
            return similar_target

        task_subquery = Q(task=self.task)

        # we can look for a 'build' task if this task is a 'populate_sdk_ext'
        # task, as the latter also creates images; and vice versa; note that
        # 'build' targets can have their task set to '';
        # also note that 'populate_sdk' does not produce image files
        image_tasks = [
            '', # aka 'build'
            'build',
            'image',
            'populate_sdk_ext'
        ]
        if self.task in image_tasks:
            task_subquery = Q(task__in=image_tasks)

        # annotate with the count of files, to exclude any targets which
        # don't have associated files
        candidates = candidates.annotate(num_files=Count('target_image_file'))

        query = task_subquery & Q(num_files__gt=0)

        candidates = candidates.filter(query)

        if candidates.count() > 0:
            candidates.order_by('build__completed_on')
            similar_target = candidates.last()

        return similar_target

    def get_similar_target_with_sdk_files(self):
        """
        Get the most recent similar target with TargetSDKFiles associated
        with it, for the purpose of cloning those files onto this target.
        """
        similar_target = None

        candidates = self.get_similar_targets()
        if candidates.count() == 0:
            return similar_target

        # annotate with the count of files, to exclude any targets which
        # don't have associated files
        candidates = candidates.annotate(num_files=Count('targetsdkfile'))

        query = Q(task=self.task) & Q(num_files__gt=0)

        candidates = candidates.filter(query)

        if candidates.count() > 0:
            candidates.order_by('build__completed_on')
            similar_target = candidates.last()

        return similar_target

    def clone_image_artifacts_from(self, target):
        """
        Make clones of the Target_Image_Files and TargetKernelFile objects
        associated with Target target, then associate them with this target.

        Note that for Target_Image_Files, we only want files from the previous
        build whose suffix matches one of the suffixes defined in this
        target's build's IMAGE_FSTYPES configuration variable. This prevents the
        Target_Image_File object for an ext4 image being associated with a
        target for a project which didn't produce an ext4 image (for example).

        Also sets the license_manifest_path and package_manifest_path
        of this target to the same path as that of target being cloned from, as
        the manifests are also build artifacts but are treated differently.
        """

        image_fstypes = self.build.get_image_fstypes()

        # filter out any image files whose suffixes aren't in the
        # IMAGE_FSTYPES suffixes variable for this target's build
        image_files = [target_image_file \
            for target_image_file in target.target_image_file_set.all() \
            if target_image_file.suffix in image_fstypes]

        for image_file in image_files:
            image_file.pk = None
            image_file.target = self
            image_file.save()

        kernel_files = target.targetkernelfile_set.all()
        for kernel_file in kernel_files:
            kernel_file.pk = None
            kernel_file.target = self
            kernel_file.save()

        self.license_manifest_path = target.license_manifest_path
        self.package_manifest_path = target.package_manifest_path
        self.save()

    def clone_sdk_artifacts_from(self, target):
        """
        Clone TargetSDKFile objects from target and associate them with this
        target.
        """
        sdk_files = target.targetsdkfile_set.all()
        for sdk_file in sdk_files:
            sdk_file.pk = None
            sdk_file.target = self
            sdk_file.save()

    def has_images(self):
        """
        Returns True if this target has one or more image files attached to it.
        """
        return self.target_image_file_set.all().count() > 0

# kernel artifacts for a target: bzImage and modules*
class TargetKernelFile(models.Model):
    target = models.ForeignKey(Target, on_delete=models.CASCADE)
    file_name = models.FilePathField()
    file_size = models.IntegerField()

    @property
    def basename(self):
        return os.path.basename(self.file_name)

# SDK artifacts for a target: sh and manifest files
class TargetSDKFile(models.Model):
    target = models.ForeignKey(Target, on_delete=models.CASCADE)
    file_name = models.FilePathField()
    file_size = models.IntegerField()

    @property
    def basename(self):
        return os.path.basename(self.file_name)

class Target_Image_File(models.Model):
    # valid suffixes for image files produced by a build
    SUFFIXES = {
        'btrfs', 'container', 'cpio', 'cpio.gz', 'cpio.lz4', 'cpio.lzma',
        'cpio.xz', 'cramfs', 'ext2', 'ext2.bz2', 'ext2.gz', 'ext2.lzma',
        'ext3', 'ext3.gz', 'ext4', 'ext4.gz', 'f2fs', 'hddimg', 'iso', 'jffs2',
        'jffs2.sum', 'multiubi', 'squashfs', 'squashfs-lz4', 'squashfs-lzo',
        'squashfs-xz', 'tar', 'tar.bz2', 'tar.gz', 'tar.lz4', 'tar.xz', 'ubi',
        'ubifs', 'wic', 'wic.bz2', 'wic.gz', 'wic.lzma'
    }

    target = models.ForeignKey(Target, on_delete=models.CASCADE)
    file_name = models.FilePathField(max_length=254)
    file_size = models.IntegerField()

    @property
    def suffix(self):
        """
        Suffix for image file, minus leading "."
        """
        for suffix in Target_Image_File.SUFFIXES:
            if self.file_name.endswith(suffix):
                return suffix

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

    target = models.ForeignKey(Target, on_delete=models.CASCADE)
    path = models.FilePathField()
    size = models.IntegerField()
    inodetype = models.IntegerField(choices = ITYPES)
    permission = models.CharField(max_length=16)
    owner = models.CharField(max_length=128)
    group = models.CharField(max_length=128)
    directory = models.ForeignKey('Target_File', on_delete=models.CASCADE, related_name="directory_set", null=True)
    sym_target = models.ForeignKey('Target_File', on_delete=models.CASCADE, related_name="symlink_set", null=True)


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

    build = models.ForeignKey(Build, on_delete=models.CASCADE, related_name='task_build')
    order = models.IntegerField(null=True)
    task_executed = models.BooleanField(default=False) # True means Executed, False means Not/Executed
    outcome = models.IntegerField(choices=TASK_OUTCOME, default=OUTCOME_NA)
    sstate_checksum = models.CharField(max_length=100, blank=True)
    path_to_sstate_obj = models.FilePathField(max_length=500, blank=True)
    recipe = models.ForeignKey('Recipe', on_delete=models.CASCADE, related_name='tasks')
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
    task = models.ForeignKey(Task, on_delete=models.CASCADE, related_name='task_dependencies_task')
    depends_on = models.ForeignKey(Task, on_delete=models.CASCADE, related_name='task_dependencies_depends')

class Package(models.Model):
    search_allowed_fields = ['name', 'version', 'revision', 'recipe__name', 'recipe__version', 'recipe__license', 'recipe__layer_version__layer__name', 'recipe__layer_version__branch', 'recipe__layer_version__commit', 'recipe__layer_version__local_path', 'installed_name']
    build = models.ForeignKey('Build', on_delete=models.CASCADE, null=True)
    recipe = models.ForeignKey('Recipe', on_delete=models.CASCADE, null=True)
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
    TARGET_LATEST = "use-latest-target-for-target"

    def get_queryset(self):
        return super(Package_DependencyManager, self).get_queryset().exclude(package_id = F('depends_on__id'))

    def for_target_or_none(self, target):
        """ filter the dependencies to be displayed by the supplied target
        if no dependences are found for the target then try None as the target
        which will return the dependences calculated without the context of a
        target e.g. non image recipes.

        returns: { size, packages }
        """
        package_dependencies = self.all_depends().order_by('depends_on__name')

        if target is self.TARGET_LATEST:
            installed_deps =\
                    package_dependencies.filter(~Q(target__target=None))
        else:
            installed_deps =\
                    package_dependencies.filter(Q(target__target=target))

        packages_list = None
        total_size = 0

        # If we have installed depdencies for this package and target then use
        # these to display
        if installed_deps.count() > 0:
            packages_list = installed_deps
            total_size = installed_deps.aggregate(
                Sum('depends_on__size'))['depends_on__size__sum']
        else:
            new_list = []
            package_names = []

            # Find dependencies for the package that we know about even if
            # it's not installed on a target e.g. from a non-image recipe
            for p in package_dependencies.filter(Q(target=None)):
                if p.depends_on.name in package_names:
                    continue
                else:
                    package_names.append(p.depends_on.name)
                    new_list.append(p.pk)
                    # while we're here we may as well total up the size to
                    # avoid iterating again
                    total_size += p.depends_on.size

            # We want to return a queryset here for consistency so pick the
            # deps from the new_list
            packages_list = package_dependencies.filter(Q(pk__in=new_list))

        return {'packages': packages_list,
                'size': total_size}

    def all_depends(self):
        """ Returns just the depends packages and not any other dep_type
        Note that this is for any target
        """
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

    package = models.ForeignKey(Package, on_delete=models.CASCADE, related_name='package_dependencies_source')
    depends_on = models.ForeignKey(Package, on_delete=models.CASCADE, related_name='package_dependencies_target')   # soft dependency
    dep_type = models.IntegerField(choices=DEPENDS_TYPE)
    target = models.ForeignKey(Target, on_delete=models.CASCADE, null=True)
    objects = Package_DependencyManager()

class Target_Installed_Package(models.Model):
    target = models.ForeignKey(Target, on_delete=models.CASCADE)
    package = models.ForeignKey(Package, on_delete=models.CASCADE, related_name='buildtargetlist_package')


class Package_File(models.Model):
    package = models.ForeignKey(Package, on_delete=models.CASCADE, related_name='buildfilelist_package')
    path = models.FilePathField(max_length=255, blank=True)
    size = models.IntegerField()


class Recipe(models.Model):
    search_allowed_fields = ['name', 'version', 'file_path', 'section',
                             'summary', 'description', 'license',
                             'layer_version__layer__name',
                             'layer_version__branch', 'layer_version__commit',
                             'layer_version__local_path',
                             'layer_version__layer_source']

    up_date = models.DateTimeField(null=True, default=None)

    name = models.CharField(max_length=100, blank=True)
    version = models.CharField(max_length=100, blank=True)
    layer_version = models.ForeignKey('Layer_Version', on_delete=models.CASCADE,
                                      related_name='recipe_layer_version')
    summary = models.TextField(blank=True)
    description = models.TextField(blank=True)
    section = models.CharField(max_length=100, blank=True)
    license = models.CharField(max_length=200, blank=True)
    homepage = models.URLField(blank=True)
    bugtracker = models.URLField(blank=True)
    file_path = models.FilePathField(max_length=255)
    pathflags = models.CharField(max_length=200, blank=True)
    is_image = models.BooleanField(default=False)

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
    recipe = models.ForeignKey(Recipe, on_delete=models.CASCADE)

class Recipe_Dependency(models.Model):
    TYPE_DEPENDS = 0
    TYPE_RDEPENDS = 1

    DEPENDS_TYPE = (
        (TYPE_DEPENDS, "depends"),
        (TYPE_RDEPENDS, "rdepends"),
    )
    recipe = models.ForeignKey(Recipe, on_delete=models.CASCADE, related_name='r_dependencies_recipe')
    depends_on = models.ForeignKey(Recipe, on_delete=models.CASCADE, related_name='r_dependencies_depends')
    via = models.ForeignKey(Provides, on_delete=models.CASCADE, null=True, default=None)
    dep_type = models.IntegerField(choices=DEPENDS_TYPE)
    objects = Recipe_DependencyManager()


class Machine(models.Model):
    search_allowed_fields = ["name", "description", "layer_version__layer__name"]
    up_date = models.DateTimeField(null = True, default = None)

    layer_version = models.ForeignKey('Layer_Version', on_delete=models.CASCADE)
    name = models.CharField(max_length=255)
    description = models.CharField(max_length=255)

    def get_vcs_machine_file_link_url(self):
        path = 'conf/machine/'+self.name+'.conf'

        return self.layer_version.get_vcs_file_link_url(path)

    def __unicode__(self):
        return "Machine " + self.name + "(" + self.description + ")"


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
    bitbake_version = models.ForeignKey(BitbakeVersion, on_delete=models.CASCADE)
    branch_name = models.CharField(max_length=50, default = "")
    helptext = models.TextField(null=True)

    def __unicode__(self):
        return "%s (%s)" % (self.name, self.branch_name)

    def __str__(self):
        return self.name

class ReleaseDefaultLayer(models.Model):
    release = models.ForeignKey(Release, on_delete=models.CASCADE)
    layer_name = models.CharField(max_length=100, default="")


class LayerSource(object):
    """ Where the layer metadata came from """
    TYPE_LOCAL = 0
    TYPE_LAYERINDEX = 1
    TYPE_IMPORTED = 2
    TYPE_BUILD = 3

    SOURCE_TYPE = (
        (TYPE_LOCAL, "local"),
        (TYPE_LAYERINDEX, "layerindex"),
        (TYPE_IMPORTED, "imported"),
        (TYPE_BUILD, "build"),
    )

    def types_dict():
        """ Turn the TYPES enums into a simple dictionary """
        dictionary = {}
        for key in LayerSource.__dict__:
            if "TYPE" in key:
                dictionary[key] = getattr(LayerSource, key)
        return dictionary


class Layer(models.Model):

    up_date = models.DateTimeField(null=True, default=timezone.now)

    name = models.CharField(max_length=100)
    layer_index_url = models.URLField()
    vcs_url = GitURLField(default=None, null=True)
    local_source_dir = models.TextField(null=True, default=None)
    vcs_web_url = models.URLField(null=True, default=None)
    vcs_web_tree_base_url = models.URLField(null=True, default=None)
    vcs_web_file_base_url = models.URLField(null=True, default=None)

    summary = models.TextField(help_text='One-line description of the layer',
                               null=True, default=None)
    description = models.TextField(null=True, default=None)

    def __unicode__(self):
        return "%s / %s " % (self.name, self.summary)


class Layer_Version(models.Model):
    """
    A Layer_Version either belongs to a single project or no project
    """
    search_allowed_fields = ["layer__name", "layer__summary",
                             "layer__description", "layer__vcs_url",
                             "dirpath", "release__name", "commit", "branch"]

    build = models.ForeignKey(Build, on_delete=models.CASCADE, related_name='layer_version_build',
                              default=None, null=True)

    layer = models.ForeignKey(Layer, on_delete=models.CASCADE, related_name='layer_version_layer')

    layer_source = models.IntegerField(choices=LayerSource.SOURCE_TYPE,
                                       default=0)

    up_date = models.DateTimeField(null=True, default=timezone.now)

    # To which metadata release does this layer version belong to
    release = models.ForeignKey(Release, on_delete=models.CASCADE, null=True, default=None)

    branch = models.CharField(max_length=80)
    commit = models.CharField(max_length=100)
    # If the layer is in a subdir
    dirpath = models.CharField(max_length=255, null=True, default=None)

    # if -1, this is a default layer
    priority = models.IntegerField(default=0)

    # where this layer exists on the filesystem
    local_path = models.FilePathField(max_length=1024, default="/")

    # Set if this layer is restricted to a particular project
    project = models.ForeignKey('Project', on_delete=models.CASCADE, null=True, default=None)

    # code lifted, with adaptations, from the layerindex-web application
    # https://git.yoctoproject.org/cgit/cgit.cgi/layerindex-web/
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
            branchname = self.release.name
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
        return self._handle_url_path(self.layer.vcs_web_file_base_url,
                                     file_path)

    def get_vcs_dirpath_link_url(self):
        if self.layer.vcs_web_tree_base_url is None:
            return None
        return self._handle_url_path(self.layer.vcs_web_tree_base_url, '')

    def get_vcs_reference(self):
        if self.commit is not None and len(self.commit) > 0:
            return self.commit
        if self.branch is not None and len(self.branch) > 0:
            return self.branch
        if self.release is not None:
            return self.release.name
        return 'N/A'

    def get_detailspage_url(self, project_id=None):
        """ returns the url to the layer details page uses own project
        field if project_id is not specified """

        if project_id is None:
            project_id = self.project.pk

        return reverse('layerdetails', args=(project_id, self.pk))

    def get_alldeps(self, project_id):
        """Get full list of unique layer dependencies."""
        def gen_layerdeps(lver, project, depth):
            if depth == 0:
                return
            for ldep in lver.dependencies.all():
                yield ldep.depends_on
                # get next level of deps recursively calling gen_layerdeps
                for subdep in gen_layerdeps(ldep.depends_on, project, depth-1):
                    yield subdep

        project = Project.objects.get(pk=project_id)
        result = []
        projectlvers = [player.layercommit for player in
                        project.projectlayer_set.all()]
        # protect against infinite layer dependency loops
        maxdepth = 20
        for dep in gen_layerdeps(self, project, maxdepth):
            # filter out duplicates and layers already belonging to the project
            if dep not in result + projectlvers:
                result.append(dep)

        return sorted(result, key=lambda x: x.layer.name)

    def __unicode__(self):
        return ("id %d belongs to layer: %s" % (self.pk, self.layer.name))

    def __str__(self):
        if self.release:
            release = self.release.name
        else:
            release = "No release set"

        return "%d %s (%s)" % (self.pk, self.layer.name, release)


class LayerVersionDependency(models.Model):

    layer_version = models.ForeignKey(Layer_Version, on_delete=models.CASCADE,
                                      related_name="dependencies")
    depends_on = models.ForeignKey(Layer_Version, on_delete=models.CASCADE,
                                   related_name="dependees")

class ProjectLayer(models.Model):
    project = models.ForeignKey(Project, on_delete=models.CASCADE)
    layercommit = models.ForeignKey(Layer_Version, on_delete=models.CASCADE, null=True)
    optional = models.BooleanField(default = True)

    def __unicode__(self):
        return "%s, %s" % (self.project.name, self.layercommit)

    class Meta:
        unique_together = (("project", "layercommit"),)

class CustomImageRecipe(Recipe):

    # CustomImageRecipe's belong to layers called:
    LAYER_NAME = "toaster-custom-images"

    search_allowed_fields = ['name']
    base_recipe = models.ForeignKey(Recipe, on_delete=models.CASCADE, related_name='based_on_recipe')
    project = models.ForeignKey(Project, on_delete=models.CASCADE)
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
        if target is None:
            # So we've never actually built this Custom recipe but what about
            # the recipe it's based on?
            target = \
                Target.objects.filter(Q(build__outcome=Build.SUCCEEDED) &
                                      Q(build__project=self.project) &
                                      Q(target=self.base_recipe.name)).last()
            if target is None:
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

    def get_base_recipe_file(self):
        """Get the base recipe file path if it exists on the file system"""
        path_schema_one = "%s/%s" % (self.base_recipe.layer_version.local_path,
                                     self.base_recipe.file_path)

        path_schema_two = self.base_recipe.file_path

        path_schema_three = "%s/%s" % (self.base_recipe.layer_version.layer.local_source_dir,
                                     self.base_recipe.file_path)

        if os.path.exists(path_schema_one):
            return path_schema_one

        # The path may now be the full path if the recipe has been built
        if os.path.exists(path_schema_two):
            return path_schema_two

        # Or a local path if all layers are local
        if os.path.exists(path_schema_three):
            return path_schema_three

        return None

    def generate_recipe_file_contents(self):
        """Generate the contents for the recipe file."""
        # If we have no excluded packages we only need to :append
        if self.excludes_set.count() == 0:
            packages_conf = "IMAGE_INSTALL:append = \" "

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

        base_recipe_path = self.get_base_recipe_file()
        if base_recipe_path and os.path.isfile(base_recipe_path):
            base_recipe = open(base_recipe_path, 'r').read()
        else:
            # Pass back None to trigger error message to user
            return None

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

            base_recipe = base_recipe.replace(req_search.group(0),
                                              new_require_line)

        info = {
            "date": timezone.now().strftime("%Y-%m-%d %H:%M:%S"),
            "base_recipe": base_recipe,
            "recipe_name": self.name,
            "base_recipe_name": self.base_recipe.name,
            "license": self.license,
            "summary": self.summary,
            "description": self.description,
            "packages_conf": packages_conf.strip()
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
    project = models.ForeignKey(Project, on_delete=models.CASCADE)
    name = models.CharField(max_length=100)
    value = models.TextField(blank = True)

class Variable(models.Model):
    search_allowed_fields = ['variable_name', 'variable_value',
                             'vhistory__file_name', "description"]
    build = models.ForeignKey(Build, on_delete=models.CASCADE, related_name='variable_build')
    variable_name = models.CharField(max_length=100)
    variable_value = models.TextField(blank=True)
    changed = models.BooleanField(default=False)
    human_readable_name = models.CharField(max_length=200)
    description = models.TextField(blank=True)

class VariableHistory(models.Model):
    variable = models.ForeignKey(Variable, on_delete=models.CASCADE, related_name='vhistory')
    value   = models.TextField(blank=True)
    file_name = models.FilePathField(max_length=255)
    line_number = models.IntegerField(null=True)
    operation = models.CharField(max_length=64)

class HelpText(models.Model):
    VARIABLE = 0
    HELPTEXT_AREA = ((VARIABLE, 'variable'), )

    build = models.ForeignKey(Build, on_delete=models.CASCADE, related_name='helptext_build')
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

    build = models.ForeignKey(Build, on_delete=models.CASCADE)
    task  = models.ForeignKey(Task, on_delete=models.CASCADE, blank = True, null=True)
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

def signal_runbuilds():
    """Send SIGUSR1 to runbuilds process"""
    try:
        with open(os.path.join(os.getenv('BUILDDIR', '.'),
                               '.runbuilds.pid')) as pidf:
            os.kill(int(pidf.read()), SIGUSR1)
    except FileNotFoundError:
        logger.info("Stopping existing runbuilds: no current process found")
    except ProcessLookupError:
        logger.warning("Stopping existing runbuilds: process lookup not found")

class Distro(models.Model):
    search_allowed_fields = ["name", "description", "layer_version__layer__name"]
    up_date = models.DateTimeField(null = True, default = None)

    layer_version = models.ForeignKey('Layer_Version', on_delete=models.CASCADE)
    name = models.CharField(max_length=255)
    description = models.CharField(max_length=255)

    def get_vcs_distro_file_link_url(self):
        path = 'conf/distro/%s.conf' % self.name
        return self.layer_version.get_vcs_file_link_url(path)

    def __unicode__(self):
        return "Distro " + self.name + "(" + self.description + ")"

class EventLogsImports(models.Model):
    name = models.CharField(max_length=255)
    imported = models.BooleanField(default=False)
    build_id = models.IntegerField(blank=True, null=True)

    def __str__(self):
        return self.name


django.db.models.signals.post_save.connect(invalidate_cache)
django.db.models.signals.post_delete.connect(invalidate_cache)
django.db.models.signals.m2m_changed.connect(invalidate_cache)
