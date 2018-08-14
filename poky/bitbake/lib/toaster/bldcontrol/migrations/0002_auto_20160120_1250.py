# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('bldcontrol', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='buildenvironment',
            name='betype',
            field=models.IntegerField(choices=[(0, b'local')]),
        ),
    ]
