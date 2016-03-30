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

from django.db import models, IntegrityError
from django.db.models import F, Q, Avg, Max
from django.utils import timezone

from django.core.urlresolvers import reverse

from django.core import validators
from django.conf import settings
import django.db.models.signals


import logging
logger = logging.getLogger("toaster")


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

    def create(self, *args, **kwargs):
        raise Exception("Invalid call to Project.objects.create. Use Project.objects.create_project() to create a project")

    # return single object with is_default = True
    def get_default_project(self):
        projects = super(ProjectManager, self).filter(is_default = True)
        if len(projects) > 1:
            raise Exception("Inconsistent project data: multiple " +
                            "default projects (i.e. with is_default=True)")
        elif len(projects) < 1:
            raise Exception("Inconsistent project data: no default project found")
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
            return( "None" );

    def get_number_of_builds(self):
        try:
            return len(Build.objects.filter( project = self.id ))
        except (Build.DoesNotExist,IndexError):
            return( 0 )

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

    def get_last_imgfiles(self):
        build_id = self.get_last_build_id
        if (-1 == build_id):
            return( "" )
        try:
            return Variable.objects.filter(build = build_id, variable_name = "IMAGE_FSTYPES")[ 0 ].variable_value
        except (Variable.DoesNotExist,IndexError):
            return( "not_found" )

    # returns a queryset of compatible layers for a project
    def compatible_layerversions(self, release = None, layer_name = None):
        logger.warning("This function is deprecated")
        if release == None:
            release = self.release
        # layers on the same branch or layers specifically set for this project
        queryset = Layer_Version.objects.filter(((Q(up_branch__name = release.branch_name) & Q(project = None)) | Q(project = self)) & Q(build__isnull=True))

        if layer_name is not None:
            # we select only a layer name
            queryset = queryset.filter(layer__name = layer_name)

        # order by layer version priority
        queryset = queryset.filter(Q(layer_source=None) | Q(layer_source__releaselayersourcepriority__release = release)).select_related('layer_source', 'layer', 'up_branch', "layer_source__releaselayersourcepriority__priority").order_by("-layer_source__releaselayersourcepriority__priority")

        return queryset

    def get_all_compatible_layer_versions(self):
        """ Returns Queryset of all Layer_Versions which are compatible with
        this project"""
        queryset = Layer_Version.objects.filter(
            (Q(up_branch__name=self.release.branch_name) & Q(build=None))
            | Q(project=self))

        return queryset

    def get_project_layer_versions(self, pk=False):
        """ Returns the Layer_Versions currently added to this project """
        layer_versions = self.projectlayer_set.all().values('layercommit')

        if pk is False:
            return layer_versions
        else:
            return layer_versions.values_list('layercommit__pk', flat=True)


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

    BUILD_OUTCOME = (
        (SUCCEEDED, 'Succeeded'),
        (FAILED, 'Failed'),
        (IN_PROGRESS, 'In Progress'),
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

    def completeper(self):
        tf = Task.objects.filter(build = self)
        tfc = tf.count()
        if tfc > 0:
            completeper = tf.exclude(order__isnull=True).count()*100/tf.count()
        else:
            completeper = 0
        return completeper

    def eta(self):
        eta = timezone.now()
        completeper = self.completeper()
        if self.completeper() > 0:
            eta += ((eta - self.started_on)*(100-completeper))/completeper
        return eta


    def get_sorted_target_list(self):
        tgts = Target.objects.filter(build_id = self.id).order_by( 'target' );
        return( tgts );

    def get_outcome_text(self):
        return Build.BUILD_OUTCOME[int(self.outcome)][1]

    @property
    def errors(self):
        return (self.logmessage_set.filter(level=LogMessage.ERROR) |
                self.logmessage_set.filter(level=LogMessage.EXCEPTION) |
                self.logmessage_set.filter(level=LogMessage.CRITICAL))

    @property
    def warnings(self):
        return self.logmessage_set.filter(level=LogMessage.WARNING)

    @property
    def timespent_seconds(self):
        return (self.completed_on - self.started_on).total_seconds()

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
    target = models.ForeignKey(Target)
    file_name = models.FilePathField(max_length=254)
    file_size = models.IntegerField()

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
    disk_io = models.IntegerField(null=True)
    cpu_usage = models.DecimalField(max_digits=8, decimal_places=2, null=True)
    elapsed_time = models.DecimalField(max_digits=8, decimal_places=2, null=True)
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

class Package_DependencyManager(models.Manager):
    use_for_related_fields = True

    def get_query_set(self):
        return super(Package_DependencyManager, self).get_query_set().exclude(package_id = F('depends_on__id'))

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

    def get_query_set(self):
        return super(Recipe_DependencyManager, self).get_query_set().exclude(recipe_id = F('depends_on__id'))

class Recipe_Dependency(models.Model):
    TYPE_DEPENDS = 0
    TYPE_RDEPENDS = 1

    DEPENDS_TYPE = (
        (TYPE_DEPENDS, "depends"),
        (TYPE_RDEPENDS, "rdepends"),
    )
    recipe = models.ForeignKey(Recipe, related_name='r_dependencies_recipe')
    depends_on = models.ForeignKey(Recipe, related_name='r_dependencies_depends')
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
        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(False)
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

        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(True)

        # update layerbranches/layer_versions
        logger.debug("Fetching layer information")
        layerbranches_info = _get_json_response(apilinks['layerBranches']
                + "?filter=branch:%s" % "OR".join(map(lambda x: str(x.up_id), [i for i in Branch.objects.filter(layer_source = self) if i.up_id is not None] ))
            )

        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(False)
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
        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(True)

        # update layer dependencies
        layerdependencies_info = _get_json_response(apilinks['layerDependencies'])
        dependlist = {}
        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(False)
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
        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(True)


        # update machines
        logger.debug("Fetching machine information")
        machines_info = _get_json_response(apilinks['machines']
                + "?filter=layerbranch:%s" % "OR".join(map(lambda x: str(x.up_id), Layer_Version.objects.filter(layer_source = self)))
            )

        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(False)
        for mi in machines_info:
            mo, created = Machine.objects.get_or_create(layer_source = self, up_id = mi['id'], layer_version = Layer_Version.objects.get(layer_source = self, up_id = mi['layerbranch']))
            mo.up_date = mi['updated']
            mo.name = mi['name']
            mo.description = mi['description']
            mo.save()

        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(True)

        # update recipes; paginate by layer version / layer branch
        logger.debug("Fetching target information")
        recipes_info = _get_json_response(apilinks['recipes']
                + "?filter=layerbranch:%s" % "OR".join(map(lambda x: str(x.up_id), Layer_Version.objects.filter(layer_source = self)))
            )
        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(False)
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
        if not connection.features.autocommits_when_autocommit_is_off:
            transaction.set_autocommit(True)

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
        return project.compatible_layerversions(layer_name = self.layer.name)

    def get_vcs_reference(self):
        if self.branch is not None and len(self.branch) > 0:
            return self.branch
        if self.up_branch is not None:
            return self.up_branch.name
        if self.commit is not None and len(self.commit) > 0:
            return self.commit
        return ("Cannot determine the vcs_reference for layer version %s" % vars(self))

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

class CustomImageRecipe(models.Model):
    name = models.CharField(max_length=100)
    base_recipe = models.ForeignKey(Recipe)
    packages = models.ManyToManyField(Package)
    project = models.ForeignKey(Project)

    class Meta:
        unique_together = ("name", "project")

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
        return "%s %s %s" % (self.get_level_display(), self.message, self.build)

def invalidate_cache(**kwargs):
    from django.core.cache import cache
    try:
      cache.clear()
    except Exception as e:
      logger.warning("Problem with cache backend: Failed to clear cache: %s" % e)

django.db.models.signals.post_save.connect(invalidate_cache)
django.db.models.signals.post_delete.connect(invalidate_cache)
