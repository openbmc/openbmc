from toastermain.settings import getDATABASE_URL
from django.core.management.base import NoArgsCommand

class Command(NoArgsCommand):
    args    = ""
    help    = "get database url"

    def handle_noargs(self,**options):
        print getDATABASE_URL()
