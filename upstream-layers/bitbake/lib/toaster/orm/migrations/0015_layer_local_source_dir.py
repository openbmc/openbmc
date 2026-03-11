# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0014_allow_empty_buildname'),
    ]

    operations = [
        migrations.AddField(
            model_name='layer',
            name='local_source_dir',
            field=models.TextField(null=True, default=None),
        ),
    ]
