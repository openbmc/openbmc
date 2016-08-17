# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0005_task_field_separation'),
    ]

    operations = [
        migrations.AlterField(
            model_name='build',
            name='outcome',
            field=models.IntegerField(default=2, choices=[(0, b'Succeeded'), (1, b'Failed'), (2, b'In Progress'), (3, b'Cancelled')]),
        ),
    ]
