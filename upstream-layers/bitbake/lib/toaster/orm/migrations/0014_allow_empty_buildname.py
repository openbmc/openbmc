# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0013_recipe_parse_progress_fields'),
    ]

    operations = [
        migrations.AlterField(
            model_name='build',
            name='build_name',
            field=models.CharField(default='', max_length=100),
        ),
    ]
