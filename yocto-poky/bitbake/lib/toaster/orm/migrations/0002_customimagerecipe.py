# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='CustomImageRecipe',
            fields=[
                ('recipe_ptr', models.OneToOneField(parent_link=True, auto_created=True, primary_key=True, serialize=False, to='orm.Recipe')),
                ('last_updated', models.DateTimeField(default=None, null=True)),
                ('base_recipe', models.ForeignKey(related_name='based_on_recipe', to='orm.Recipe')),
                ('project', models.ForeignKey(to='orm.Project')),
            ],
            bases=('orm.recipe',),
        ),
    ]
