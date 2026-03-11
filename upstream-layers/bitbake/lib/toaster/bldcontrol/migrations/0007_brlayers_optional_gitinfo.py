# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('bldcontrol', '0006_brlayer_local_source_dir'),
    ]

    operations = [
        migrations.AlterField(
            model_name='brlayer',
            name='commit',
            field=models.CharField(max_length=254, null=True),
        ),
        migrations.AlterField(
            model_name='brlayer',
            name='dirpath',
            field=models.CharField(max_length=254, null=True),
        ),
        migrations.AlterField(
            model_name='brlayer',
            name='giturl',
            field=models.CharField(max_length=254, null=True),
        ),
    ]
