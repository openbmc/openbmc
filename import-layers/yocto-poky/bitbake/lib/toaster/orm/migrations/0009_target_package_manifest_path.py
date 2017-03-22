# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0008_refactor_artifact_models'),
    ]

    operations = [
        migrations.AddField(
            model_name='target',
            name='package_manifest_path',
            field=models.CharField(null=True, max_length=500),
        ),
    ]
