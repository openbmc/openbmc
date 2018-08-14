# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0012_use_release_instead_of_up_branch'),
    ]

    operations = [
        migrations.AddField(
            model_name='build',
            name='recipes_parsed',
            field=models.IntegerField(default=0),
        ),
        migrations.AddField(
            model_name='build',
            name='recipes_to_parse',
            field=models.IntegerField(default=1),
        ),
    ]
