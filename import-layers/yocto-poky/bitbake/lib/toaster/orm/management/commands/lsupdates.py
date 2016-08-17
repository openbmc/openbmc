from django.core.management.base import NoArgsCommand, CommandError
from orm.models import LayerSource
import os

class Command(NoArgsCommand):
    args    = ""
    help    = "Updates locally cached information from all LayerSources"


    def handle_noargs(self, **options):
        for ls in LayerSource.objects.all():
            ls.update()
