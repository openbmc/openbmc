from django.db import models
from django.core.validators import MaxValueValidator, MinValueValidator
from orm.models import Project, ProjectLayer, ProjectVariable, ProjectTarget, Build, Layer_Version

# a BuildEnvironment is the equivalent of the "build/" directory on the localhost
class BuildEnvironment(models.Model):
    SERVER_STOPPED = 0
    SERVER_STARTED = 1
    SERVER_STATE = (
        (SERVER_STOPPED, "stopped"),
        (SERVER_STARTED, "started"),
    )

    TYPE_LOCAL = 0
    TYPE_SSH   = 1
    TYPE = (
        (TYPE_LOCAL, "local"),
        (TYPE_SSH, "ssh"),
    )

    LOCK_FREE = 0
    LOCK_LOCK = 1
    LOCK_RUNNING = 2
    LOCK_STATE = (
        (LOCK_FREE, "free"),
        (LOCK_LOCK, "lock"),
        (LOCK_RUNNING, "running"),
    )

    address     = models.CharField(max_length = 254)
    betype      = models.IntegerField(choices = TYPE)
    bbaddress   = models.CharField(max_length = 254, blank = True)
    bbport      = models.IntegerField(default = -1)
    bbtoken     = models.CharField(max_length = 126, blank = True)
    bbstate     = models.IntegerField(choices = SERVER_STATE, default = SERVER_STOPPED)
    sourcedir   = models.CharField(max_length = 512, blank = True)
    builddir    = models.CharField(max_length = 512, blank = True)
    lock        = models.IntegerField(choices = LOCK_STATE, default = LOCK_FREE)
    created     = models.DateTimeField(auto_now_add = True)
    updated     = models.DateTimeField(auto_now = True)

    def get_artifact(self, path):
        if self.betype == BuildEnvironment.TYPE_LOCAL:
            return open(path, "r")
        raise Exception("FIXME: artifact download not implemented for build environment type %s" % self.get_betype_display())

    def has_artifact(self, path):
        import os
        if self.betype == BuildEnvironment.TYPE_LOCAL:
            return os.path.exists(path)
        raise Exception("FIXME: has artifact not implemented for build environment type %s" % self.get_betype_display())

# a BuildRequest is a request that the scheduler will build using a BuildEnvironment
# the build request queue is the table itself, ordered by state

class BuildRequest(models.Model):
    REQ_CREATED = 0
    REQ_QUEUED = 1
    REQ_INPROGRESS = 2
    REQ_COMPLETED = 3
    REQ_FAILED = 4
    REQ_DELETED = 5
    REQ_ARCHIVE = 6

    REQUEST_STATE = (
        (REQ_CREATED, "created"),
        (REQ_QUEUED, "queued"),
        (REQ_INPROGRESS, "in progress"),
        (REQ_COMPLETED, "completed"),
        (REQ_FAILED, "failed"),
        (REQ_DELETED, "deleted"),
        (REQ_ARCHIVE, "archive"),
    )

    search_allowed_fields = ("brtarget__target", "build__project__name")

    project     = models.ForeignKey(Project)
    build       = models.OneToOneField(Build, null = True)     # TODO: toasterui should set this when Build is created
    environment = models.ForeignKey(BuildEnvironment, null = True)
    state       = models.IntegerField(choices = REQUEST_STATE, default = REQ_CREATED)
    created     = models.DateTimeField(auto_now_add = True)
    updated     = models.DateTimeField(auto_now = True)

    def get_duration(self):
        return (self.updated - self.created).total_seconds()

    def get_sorted_target_list(self):
        tgts = self.brtarget_set.order_by( 'target' );
        return( tgts );

    def get_machine(self):
        return self.brvariable_set.get(name="MACHINE").value

    def __str__(self):
        return "%s %s" % (self.project, self.get_state_display())

# These tables specify the settings for running an actual build.
# They MUST be kept in sync with the tables in orm.models.Project*

class BRLayer(models.Model):
    req         = models.ForeignKey(BuildRequest)
    name        = models.CharField(max_length = 100)
    giturl      = models.CharField(max_length = 254)
    commit      = models.CharField(max_length = 254)
    dirpath     = models.CharField(max_length = 254)
    layer_version = models.ForeignKey(Layer_Version, null=True)

class BRBitbake(models.Model):
    req         = models.ForeignKey(BuildRequest, unique = True)    # only one bitbake for a request
    giturl      = models.CharField(max_length =254)
    commit      = models.CharField(max_length = 254)
    dirpath     = models.CharField(max_length = 254)

class BRVariable(models.Model):
    req         = models.ForeignKey(BuildRequest)
    name        = models.CharField(max_length=100)
    value       = models.TextField(blank = True)

class BRTarget(models.Model):
    req         = models.ForeignKey(BuildRequest)
    target      = models.CharField(max_length=100)
    task        = models.CharField(max_length=100, null=True)

class BRError(models.Model):
    req         = models.ForeignKey(BuildRequest)
    errtype     = models.CharField(max_length=100)
    errmsg      = models.TextField()
    traceback   = models.TextField()

    def __str__(self):
        return "%s (%s)" % (self.errmsg, self.req)
