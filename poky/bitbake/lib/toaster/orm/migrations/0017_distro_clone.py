# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models

class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0016_clone_progress'),
    ]

    operations = [
        migrations.CreateModel(
            name='Distro',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('up_id', models.IntegerField(default=None, null=True)),
                ('up_date', models.DateTimeField(default=None, null=True)),
                ('name', models.CharField(max_length=255)),
                ('description', models.CharField(max_length=255)),
                ('layer_version', models.ForeignKey(to='orm.Layer_Version', on_delete=models.CASCADE)),
            ],
        ),
    ]

