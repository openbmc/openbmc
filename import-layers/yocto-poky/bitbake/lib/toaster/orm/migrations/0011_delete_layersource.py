# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0010_delete_layer_source_references'),
    ]

    operations = [
        migrations.DeleteModel(
            name='LayerSource',
        ),
    ]
