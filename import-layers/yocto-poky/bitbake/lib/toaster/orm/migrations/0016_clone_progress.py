# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models

class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0015_layer_local_source_dir'),
    ]

    operations = [
        migrations.AddField(
            model_name='build',
            name='repos_cloned',
            field=models.IntegerField(default=1),
        ),
        migrations.AddField(
            model_name='build',
            name='repos_to_clone',
            field=models.IntegerField(default=1), # (default off)
        ),
    ]

