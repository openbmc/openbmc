# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0003_customimagepackage'),
    ]

    operations = [
        migrations.CreateModel(
            name='Provides',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=100)),
                ('recipe', models.ForeignKey(to='orm.Recipe')),
            ],
        ),
        migrations.AddField(
            model_name='recipe_dependency',
            name='via',
            field=models.ForeignKey(null=True, default=None, to='orm.Provides'),
        ),
    ]
