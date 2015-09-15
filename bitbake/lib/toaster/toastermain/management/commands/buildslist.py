from django.core.management.base import NoArgsCommand, CommandError
from orm.models import Build
import os



class Command(NoArgsCommand):
    args    = ""
    help    = "Lists current builds"

    def handle_noargs(self,**options):
        for b in Build.objects.all():
            print "%d: %s %s %s" % (b.pk, b.machine, b.distro, ",".join([x.target for x in b.target_set.all()]))
