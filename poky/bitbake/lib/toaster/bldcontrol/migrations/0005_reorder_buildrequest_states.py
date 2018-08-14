# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('bldcontrol', '0004_auto_20160523_1446'),
    ]

    operations = [
        migrations.AlterField(
            model_name='buildrequest',
            name='state',
            field=models.IntegerField(choices=[(0, 'created'), (1, 'queued'), (2, 'in progress'), (3, 'failed'), (4, 'deleted'), (5, 'cancelling'), (6, 'completed'), (7, 'archive')], default=0),
        ),
    ]
