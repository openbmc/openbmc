# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0002_customimagerecipe'),
    ]

    operations = [
        migrations.CreateModel(
            name='CustomImagePackage',
            fields=[
                ('package_ptr', models.OneToOneField(parent_link=True, auto_created=True, primary_key=True, serialize=False, to='orm.Package', on_delete=models.CASCADE)),
                ('recipe_appends', models.ManyToManyField(related_name='appends_set', to='orm.CustomImageRecipe')),
                ('recipe_excludes', models.ManyToManyField(related_name='excludes_set', to='orm.CustomImageRecipe')),
                ('recipe_includes', models.ManyToManyField(related_name='includes_set', to='orm.CustomImageRecipe')),
            ],
            bases=('orm.package',),
        ),
    ]
