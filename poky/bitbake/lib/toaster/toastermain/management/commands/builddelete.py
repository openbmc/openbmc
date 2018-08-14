from django.core.management.base import BaseCommand, CommandError
from django.core.exceptions import ObjectDoesNotExist
from orm.models import Build
from django.db import OperationalError
import os



class Command(BaseCommand):
    args    = '<buildID1 buildID2 .....>'
    help    = "Deletes selected build(s)"

    def handle(self, *args, **options):
        for bid in args:
            try:
                b = Build.objects.get(pk = bid)
            except ObjectDoesNotExist:
                print('build %s does not exist, skipping...' %(bid))
                continue
            # theoretically, just b.delete() would suffice
            # however SQLite runs into problems when you try to
            # delete too many rows at once, so we delete some direct
            # relationships from Build manually.
            for t in b.target_set.all():
                t.delete()
            for t in b.task_build.all():
                t.delete()
            for p in b.package_set.all():
                p.delete()
            for lv in b.layer_version_build.all():
                lv.delete()
            for v in b.variable_build.all():
                v.delete()
            for l in b.logmessage_set.all():
                l.delete()

            # delete the build; some databases might have had problem with migration of the bldcontrol app
            retry_count = 0
            need_bldcontrol_migration = False
            while True:
                if retry_count >= 5:
                    break
                retry_count += 1
                if need_bldcontrol_migration:
                    from django.core import management
                    management.call_command('migrate', 'bldcontrol', interactive=False)

                try:
                    b.delete()
                    break
                except OperationalError as e:
                    # execute migrations
                    need_bldcontrol_migration = True

