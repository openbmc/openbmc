# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0007_auto_20160523_1446'),
    ]

    operations = [
        migrations.CreateModel(
            name='TargetKernelFile',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, verbose_name='ID', serialize=False)),
                ('file_name', models.FilePathField()),
                ('file_size', models.IntegerField()),
                ('target', models.ForeignKey(to='orm.Target', on_delete=models.CASCADE)),
            ],
        ),
        migrations.CreateModel(
            name='TargetSDKFile',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, verbose_name='ID', serialize=False)),
                ('file_name', models.FilePathField()),
                ('file_size', models.IntegerField()),
                ('target', models.ForeignKey(to='orm.Target', on_delete=models.CASCADE)),
            ],
        ),
        migrations.RemoveField(
            model_name='buildartifact',
            name='build',
        ),
        migrations.DeleteModel(
            name='BuildArtifact',
        ),
    ]
