# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models

class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0017_distro_clone'),
    ]

    operations = [
        migrations.AddField(
            model_name='Project',
            name='builddir',
            field=models.TextField(),
        ),
        migrations.AddField(
            model_name='Project',
            name='merged_attr',
            field=models.BooleanField(default=False)
        ),
        migrations.AddField(
            model_name='Build',
            name='progress_item',
            field=models.CharField(max_length=40)
        ),
    ]
