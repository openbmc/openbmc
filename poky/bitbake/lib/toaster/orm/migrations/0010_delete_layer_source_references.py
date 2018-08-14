# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0009_target_package_manifest_path'),
    ]

    operations = [
        migrations.AlterUniqueTogether(
            name='releaselayersourcepriority',
            unique_together=set([]),
        ),
        migrations.RemoveField(
            model_name='releaselayersourcepriority',
            name='layer_source',
        ),
        migrations.RemoveField(
            model_name='releaselayersourcepriority',
            name='release',
        ),
        migrations.DeleteModel(
            name='ImportedLayerSource',
        ),
        migrations.DeleteModel(
            name='LayerIndexLayerSource',
        ),
        migrations.DeleteModel(
            name='LocalLayerSource',
        ),
        migrations.RemoveField(
            model_name='recipe',
            name='layer_source',
        ),
        migrations.RemoveField(
            model_name='recipe',
            name='up_id',
        ),
        migrations.AlterField(
            model_name='layer',
            name='up_date',
            field=models.DateTimeField(default=django.utils.timezone.now, null=True),
        ),
        migrations.AlterField(
            model_name='layer_version',
            name='layer_source',
            field=models.IntegerField(default=0, choices=[(0, 'local'), (1, 'layerindex'), (2, 'imported'), (3, 'build')]),
        ),
        migrations.AlterField(
            model_name='layer_version',
            name='up_date',
            field=models.DateTimeField(default=django.utils.timezone.now, null=True),
        ),
        migrations.AlterUniqueTogether(
            name='branch',
            unique_together=set([]),
        ),
        migrations.AlterUniqueTogether(
            name='layer',
            unique_together=set([]),
        ),
        migrations.AlterUniqueTogether(
            name='layer_version',
            unique_together=set([]),
        ),
        migrations.AlterUniqueTogether(
            name='layerversiondependency',
            unique_together=set([]),
        ),
        migrations.AlterUniqueTogether(
            name='machine',
            unique_together=set([]),
        ),
        migrations.DeleteModel(
            name='ReleaseLayerSourcePriority',
        ),
        migrations.RemoveField(
            model_name='branch',
            name='layer_source',
        ),
        migrations.RemoveField(
            model_name='branch',
            name='up_id',
        ),
        migrations.RemoveField(
            model_name='layer',
            name='layer_source',
        ),
        migrations.RemoveField(
            model_name='layer',
            name='up_id',
        ),
        migrations.RemoveField(
            model_name='layer_version',
            name='up_id',
        ),
        migrations.RemoveField(
            model_name='layerversiondependency',
            name='layer_source',
        ),
        migrations.RemoveField(
            model_name='layerversiondependency',
            name='up_id',
        ),
        migrations.RemoveField(
            model_name='machine',
            name='layer_source',
        ),
        migrations.RemoveField(
            model_name='machine',
            name='up_id',
        ),
    ]
