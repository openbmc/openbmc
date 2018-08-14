# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0004_provides'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='task',
            name='cpu_usage',
        ),
        migrations.AddField(
            model_name='task',
            name='cpu_time_system',
            field=models.DecimalField(null=True, max_digits=8, decimal_places=2),
        ),
        migrations.AddField(
            model_name='task',
            name='cpu_time_user',
            field=models.DecimalField(null=True, max_digits=8, decimal_places=2),
        ),
        migrations.AddField(
            model_name='task',
            name='disk_io_read',
            field=models.IntegerField(null=True),
        ),
        migrations.AddField(
            model_name='task',
            name='disk_io_write',
            field=models.IntegerField(null=True),
        ),
        migrations.AddField(
            model_name='task',
            name='ended',
            field=models.DateTimeField(null=True),
        ),
        migrations.AddField(
            model_name='task',
            name='started',
            field=models.DateTimeField(null=True),
        ),
    ]
