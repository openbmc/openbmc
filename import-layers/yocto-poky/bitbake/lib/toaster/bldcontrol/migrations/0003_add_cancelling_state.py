# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('bldcontrol', '0002_auto_20160120_1250'),
    ]

    operations = [
        migrations.AlterField(
            model_name='buildrequest',
            name='state',
            field=models.IntegerField(default=0, choices=[(0, b'created'), (1, b'queued'), (2, b'in progress'), (3, b'completed'), (4, b'failed'), (5, b'deleted'), (6, b'cancelling'), (7, b'archive')]),
        ),
    ]
