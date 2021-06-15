# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.db.models import Q


def branch_to_release(apps, schema_editor):
    Layer_Version = apps.get_model('orm', 'Layer_Version')
    Release = apps.get_model('orm', 'Release')

    print("Converting all layer version up_branches to releases")
    # Find all the layer versions which have an upbranch and convert them to
    # the release that they're for.
    for layer_version in Layer_Version.objects.filter(
            Q(release=None) & ~Q(up_branch=None)):
        try:
            # HEAD and local are equivalent
            if "HEAD" in layer_version.up_branch.name:
                release = Release.objects.get(name="local")
                layer_version.commit = "HEAD"
                layer_version.branch = "HEAD"
            else:
                release = Release.objects.get(
                    name=layer_version.up_branch.name)

            layer_version.release = release
            layer_version.save()
        except Exception as e:
            print("Couldn't work out an appropriate release for %s "
                  "the up_branch was %s "
                  "user the django admin interface to correct it" %
                  (layer_version.layer.name, layer_version.up_branch.name))
            print(e)

            continue


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0011_delete_layersource'),
    ]

    operations = [
        migrations.AddField(
            model_name='layer_version',
            name='release',
            field=models.ForeignKey(to='orm.Release', default=None, null=True, on_delete=models.CASCADE),
        ),
        migrations.RunPython(branch_to_release,
                             reverse_code=migrations.RunPython.noop),

        migrations.RemoveField(
            model_name='layer_version',
            name='up_branch',
        ),

        migrations.DeleteModel(
            name='Branch',
        ),
    ]
