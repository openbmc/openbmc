# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='BitbakeVersion',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(unique=True, max_length=32)),
                ('giturl', models.URLField()),
                ('branch', models.CharField(max_length=32)),
                ('dirpath', models.CharField(max_length=255)),
            ],
        ),
        migrations.CreateModel(
            name='Branch',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('up_id', models.IntegerField(default=None, null=True)),
                ('up_date', models.DateTimeField(default=None, null=True)),
                ('name', models.CharField(max_length=50)),
                ('short_description', models.CharField(max_length=50, blank=True)),
            ],
            options={
                'verbose_name_plural': 'Branches',
            },
        ),
        migrations.CreateModel(
            name='Build',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('machine', models.CharField(max_length=100)),
                ('distro', models.CharField(max_length=100)),
                ('distro_version', models.CharField(max_length=100)),
                ('started_on', models.DateTimeField()),
                ('completed_on', models.DateTimeField()),
                ('outcome', models.IntegerField(default=2, choices=[(0, b'Succeeded'), (1, b'Failed'), (2, b'In Progress')])),
                ('cooker_log_path', models.CharField(max_length=500)),
                ('build_name', models.CharField(max_length=100)),
                ('bitbake_version', models.CharField(max_length=50)),
            ],
        ),
        migrations.CreateModel(
            name='BuildArtifact',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('file_name', models.FilePathField()),
                ('file_size', models.IntegerField()),
                ('build', models.ForeignKey(to='orm.Build')),
            ],
        ),
        migrations.CreateModel(
            name='HelpText',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('area', models.IntegerField(choices=[(0, b'variable')])),
                ('key', models.CharField(max_length=100)),
                ('text', models.TextField()),
                ('build', models.ForeignKey(related_name='helptext_build', to='orm.Build')),
            ],
        ),
        migrations.CreateModel(
            name='Layer',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('up_id', models.IntegerField(default=None, null=True)),
                ('up_date', models.DateTimeField(default=None, null=True)),
                ('name', models.CharField(max_length=100)),
                ('layer_index_url', models.URLField()),
                ('vcs_url', models.URLField(default=None, null=True)),
                ('vcs_web_url', models.URLField(default=None, null=True)),
                ('vcs_web_tree_base_url', models.URLField(default=None, null=True)),
                ('vcs_web_file_base_url', models.URLField(default=None, null=True)),
                ('summary', models.TextField(default=None, help_text=b'One-line description of the layer', null=True)),
                ('description', models.TextField(default=None, null=True)),
            ],
        ),
        migrations.CreateModel(
            name='Layer_Version',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('up_id', models.IntegerField(default=None, null=True)),
                ('up_date', models.DateTimeField(default=None, null=True)),
                ('branch', models.CharField(max_length=80)),
                ('commit', models.CharField(max_length=100)),
                ('dirpath', models.CharField(default=None, max_length=255, null=True)),
                ('priority', models.IntegerField(default=0)),
                ('local_path', models.FilePathField(default=b'/', max_length=1024)),
                ('build', models.ForeignKey(related_name='layer_version_build', default=None, to='orm.Build', null=True)),
                ('layer', models.ForeignKey(related_name='layer_version_layer', to='orm.Layer')),
            ],
        ),
        migrations.CreateModel(
            name='LayerSource',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(unique=True, max_length=63)),
                ('sourcetype', models.IntegerField(choices=[(0, b'local'), (1, b'layerindex'), (2, b'imported')])),
                ('apiurl', models.CharField(default=None, max_length=255, null=True)),
            ],
        ),
        migrations.CreateModel(
            name='LayerVersionDependency',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('up_id', models.IntegerField(default=None, null=True)),
                ('depends_on', models.ForeignKey(related_name='dependees', to='orm.Layer_Version')),
                ('layer_source', models.ForeignKey(default=None, to='orm.LayerSource', null=True)),
                ('layer_version', models.ForeignKey(related_name='dependencies', to='orm.Layer_Version')),
            ],
        ),
        migrations.CreateModel(
            name='LogMessage',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('level', models.IntegerField(default=0, choices=[(0, b'info'), (1, b'warn'), (2, b'error'), (3, b'critical'), (-1, b'toaster exception')])),
                ('message', models.TextField(null=True, blank=True)),
                ('pathname', models.FilePathField(max_length=255, blank=True)),
                ('lineno', models.IntegerField(null=True)),
                ('build', models.ForeignKey(to='orm.Build')),
            ],
        ),
        migrations.CreateModel(
            name='Machine',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('up_id', models.IntegerField(default=None, null=True)),
                ('up_date', models.DateTimeField(default=None, null=True)),
                ('name', models.CharField(max_length=255)),
                ('description', models.CharField(max_length=255)),
                ('layer_source', models.ForeignKey(default=None, to='orm.LayerSource', null=True)),
                ('layer_version', models.ForeignKey(to='orm.Layer_Version')),
            ],
        ),
        migrations.CreateModel(
            name='Package',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=100)),
                ('installed_name', models.CharField(default=b'', max_length=100)),
                ('version', models.CharField(max_length=100, blank=True)),
                ('revision', models.CharField(max_length=32, blank=True)),
                ('summary', models.TextField(blank=True)),
                ('description', models.TextField(blank=True)),
                ('size', models.IntegerField(default=0)),
                ('installed_size', models.IntegerField(default=0)),
                ('section', models.CharField(max_length=80, blank=True)),
                ('license', models.CharField(max_length=80, blank=True)),
                ('build', models.ForeignKey(to='orm.Build', null=True)),
            ],
        ),
        migrations.CreateModel(
            name='Package_Dependency',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('dep_type', models.IntegerField(choices=[(0, b'depends'), (1, b'depends'), (3, b'recommends'), (2, b'recommends'), (4, b'suggests'), (5, b'provides'), (6, b'replaces'), (7, b'conflicts')])),
                ('depends_on', models.ForeignKey(related_name='package_dependencies_target', to='orm.Package')),
                ('package', models.ForeignKey(related_name='package_dependencies_source', to='orm.Package')),
            ],
        ),
        migrations.CreateModel(
            name='Package_File',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('path', models.FilePathField(max_length=255, blank=True)),
                ('size', models.IntegerField()),
                ('package', models.ForeignKey(related_name='buildfilelist_package', to='orm.Package')),
            ],
        ),
        migrations.CreateModel(
            name='Project',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=100)),
                ('short_description', models.CharField(max_length=50, blank=True)),
                ('created', models.DateTimeField(auto_now_add=True)),
                ('updated', models.DateTimeField(auto_now=True)),
                ('user_id', models.IntegerField(null=True)),
                ('is_default', models.BooleanField(default=False)),
                ('bitbake_version', models.ForeignKey(to='orm.BitbakeVersion', null=True)),
            ],
        ),
        migrations.CreateModel(
            name='ProjectLayer',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('optional', models.BooleanField(default=True)),
                ('layercommit', models.ForeignKey(to='orm.Layer_Version', null=True)),
                ('project', models.ForeignKey(to='orm.Project')),
            ],
        ),
        migrations.CreateModel(
            name='ProjectTarget',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('target', models.CharField(max_length=100)),
                ('task', models.CharField(max_length=100, null=True)),
                ('project', models.ForeignKey(to='orm.Project')),
            ],
        ),
        migrations.CreateModel(
            name='ProjectVariable',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=100)),
                ('value', models.TextField(blank=True)),
                ('project', models.ForeignKey(to='orm.Project')),
            ],
        ),
        migrations.CreateModel(
            name='Recipe',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('up_id', models.IntegerField(default=None, null=True)),
                ('up_date', models.DateTimeField(default=None, null=True)),
                ('name', models.CharField(max_length=100, blank=True)),
                ('version', models.CharField(max_length=100, blank=True)),
                ('summary', models.TextField(blank=True)),
                ('description', models.TextField(blank=True)),
                ('section', models.CharField(max_length=100, blank=True)),
                ('license', models.CharField(max_length=200, blank=True)),
                ('homepage', models.URLField(blank=True)),
                ('bugtracker', models.URLField(blank=True)),
                ('file_path', models.FilePathField(max_length=255)),
                ('pathflags', models.CharField(max_length=200, blank=True)),
                ('is_image', models.BooleanField(default=False)),
                ('layer_source', models.ForeignKey(default=None, to='orm.LayerSource', null=True)),
                ('layer_version', models.ForeignKey(related_name='recipe_layer_version', to='orm.Layer_Version')),
            ],
        ),
        migrations.CreateModel(
            name='Recipe_Dependency',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('dep_type', models.IntegerField(choices=[(0, b'depends'), (1, b'rdepends')])),
                ('depends_on', models.ForeignKey(related_name='r_dependencies_depends', to='orm.Recipe')),
                ('recipe', models.ForeignKey(related_name='r_dependencies_recipe', to='orm.Recipe')),
            ],
        ),
        migrations.CreateModel(
            name='Release',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(unique=True, max_length=32)),
                ('description', models.CharField(max_length=255)),
                ('branch_name', models.CharField(default=b'', max_length=50)),
                ('helptext', models.TextField(null=True)),
                ('bitbake_version', models.ForeignKey(to='orm.BitbakeVersion')),
            ],
        ),
        migrations.CreateModel(
            name='ReleaseDefaultLayer',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('layer_name', models.CharField(default=b'', max_length=100)),
                ('release', models.ForeignKey(to='orm.Release')),
            ],
        ),
        migrations.CreateModel(
            name='ReleaseLayerSourcePriority',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('priority', models.IntegerField(default=0)),
                ('layer_source', models.ForeignKey(to='orm.LayerSource')),
                ('release', models.ForeignKey(to='orm.Release')),
            ],
        ),
        migrations.CreateModel(
            name='Target',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('target', models.CharField(max_length=100)),
                ('task', models.CharField(max_length=100, null=True)),
                ('is_image', models.BooleanField(default=False)),
                ('image_size', models.IntegerField(default=0)),
                ('license_manifest_path', models.CharField(max_length=500, null=True)),
                ('build', models.ForeignKey(to='orm.Build')),
            ],
        ),
        migrations.CreateModel(
            name='Target_File',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('path', models.FilePathField()),
                ('size', models.IntegerField()),
                ('inodetype', models.IntegerField(choices=[(1, b'regular'), (2, b'directory'), (3, b'symlink'), (4, b'socket'), (5, b'fifo'), (6, b'character'), (7, b'block')])),
                ('permission', models.CharField(max_length=16)),
                ('owner', models.CharField(max_length=128)),
                ('group', models.CharField(max_length=128)),
                ('directory', models.ForeignKey(related_name='directory_set', to='orm.Target_File', null=True)),
                ('sym_target', models.ForeignKey(related_name='symlink_set', to='orm.Target_File', null=True)),
                ('target', models.ForeignKey(to='orm.Target')),
            ],
        ),
        migrations.CreateModel(
            name='Target_Image_File',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('file_name', models.FilePathField(max_length=254)),
                ('file_size', models.IntegerField()),
                ('target', models.ForeignKey(to='orm.Target')),
            ],
        ),
        migrations.CreateModel(
            name='Target_Installed_Package',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('package', models.ForeignKey(related_name='buildtargetlist_package', to='orm.Package')),
                ('target', models.ForeignKey(to='orm.Target')),
            ],
        ),
        migrations.CreateModel(
            name='Task',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('order', models.IntegerField(null=True)),
                ('task_executed', models.BooleanField(default=False)),
                ('outcome', models.IntegerField(default=-1, choices=[(-1, b'Not Available'), (0, b'Succeeded'), (1, b'Covered'), (2, b'Cached'), (3, b'Prebuilt'), (4, b'Failed'), (5, b'Empty')])),
                ('sstate_checksum', models.CharField(max_length=100, blank=True)),
                ('path_to_sstate_obj', models.FilePathField(max_length=500, blank=True)),
                ('task_name', models.CharField(max_length=100)),
                ('source_url', models.FilePathField(max_length=255, blank=True)),
                ('work_directory', models.FilePathField(max_length=255, blank=True)),
                ('script_type', models.IntegerField(default=0, choices=[(0, b'N/A'), (2, b'Python'), (3, b'Shell')])),
                ('line_number', models.IntegerField(default=0)),
                ('disk_io', models.IntegerField(null=True)),
                ('cpu_usage', models.DecimalField(null=True, max_digits=8, decimal_places=2)),
                ('elapsed_time', models.DecimalField(null=True, max_digits=8, decimal_places=2)),
                ('sstate_result', models.IntegerField(default=0, choices=[(0, b'Not Applicable'), (1, b'File not in cache'), (2, b'Failed'), (3, b'Succeeded')])),
                ('message', models.CharField(max_length=240)),
                ('logfile', models.FilePathField(max_length=255, blank=True)),
                ('build', models.ForeignKey(related_name='task_build', to='orm.Build')),
                ('recipe', models.ForeignKey(related_name='tasks', to='orm.Recipe')),
            ],
            options={
                'ordering': ('order', 'recipe'),
            },
        ),
        migrations.CreateModel(
            name='Task_Dependency',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('depends_on', models.ForeignKey(related_name='task_dependencies_depends', to='orm.Task')),
                ('task', models.ForeignKey(related_name='task_dependencies_task', to='orm.Task')),
            ],
        ),
        migrations.CreateModel(
            name='ToasterSetting',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=63)),
                ('helptext', models.TextField()),
                ('value', models.CharField(max_length=255)),
            ],
        ),
        migrations.CreateModel(
            name='Variable',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('variable_name', models.CharField(max_length=100)),
                ('variable_value', models.TextField(blank=True)),
                ('changed', models.BooleanField(default=False)),
                ('human_readable_name', models.CharField(max_length=200)),
                ('description', models.TextField(blank=True)),
                ('build', models.ForeignKey(related_name='variable_build', to='orm.Build')),
            ],
        ),
        migrations.CreateModel(
            name='VariableHistory',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('value', models.TextField(blank=True)),
                ('file_name', models.FilePathField(max_length=255)),
                ('line_number', models.IntegerField(null=True)),
                ('operation', models.CharField(max_length=64)),
                ('variable', models.ForeignKey(related_name='vhistory', to='orm.Variable')),
            ],
        ),
        migrations.AddField(
            model_name='project',
            name='release',
            field=models.ForeignKey(to='orm.Release', null=True),
        ),
        migrations.AddField(
            model_name='package_dependency',
            name='target',
            field=models.ForeignKey(to='orm.Target', null=True),
        ),
        migrations.AddField(
            model_name='package',
            name='recipe',
            field=models.ForeignKey(to='orm.Recipe', null=True),
        ),
        migrations.AddField(
            model_name='logmessage',
            name='task',
            field=models.ForeignKey(blank=True, to='orm.Task', null=True),
        ),
        migrations.AlterUniqueTogether(
            name='layersource',
            unique_together=set([('sourcetype', 'apiurl')]),
        ),
        migrations.AddField(
            model_name='layer_version',
            name='layer_source',
            field=models.ForeignKey(default=None, to='orm.LayerSource', null=True),
        ),
        migrations.AddField(
            model_name='layer_version',
            name='project',
            field=models.ForeignKey(default=None, to='orm.Project', null=True),
        ),
        migrations.AddField(
            model_name='layer_version',
            name='up_branch',
            field=models.ForeignKey(default=None, to='orm.Branch', null=True),
        ),
        migrations.AddField(
            model_name='layer',
            name='layer_source',
            field=models.ForeignKey(default=None, to='orm.LayerSource', null=True),
        ),
        migrations.AddField(
            model_name='build',
            name='project',
            field=models.ForeignKey(to='orm.Project'),
        ),
        migrations.AddField(
            model_name='branch',
            name='layer_source',
            field=models.ForeignKey(default=True, to='orm.LayerSource', null=True),
        ),
        migrations.CreateModel(
            name='ImportedLayerSource',
            fields=[
            ],
            options={
                'proxy': True,
            },
            bases=('orm.layersource',),
        ),
        migrations.CreateModel(
            name='LayerIndexLayerSource',
            fields=[
            ],
            options={
                'proxy': True,
            },
            bases=('orm.layersource',),
        ),
        migrations.CreateModel(
            name='LocalLayerSource',
            fields=[
            ],
            options={
                'proxy': True,
            },
            bases=('orm.layersource',),
        ),
        migrations.AlterUniqueTogether(
            name='task',
            unique_together=set([('build', 'recipe', 'task_name')]),
        ),
        migrations.AlterUniqueTogether(
            name='releaselayersourcepriority',
            unique_together=set([('release', 'layer_source')]),
        ),
        migrations.AlterUniqueTogether(
            name='recipe',
            unique_together=set([('layer_version', 'file_path', 'pathflags')]),
        ),
        migrations.AlterUniqueTogether(
            name='projectlayer',
            unique_together=set([('project', 'layercommit')]),
        ),
        migrations.AlterUniqueTogether(
            name='machine',
            unique_together=set([('layer_source', 'up_id')]),
        ),
        migrations.AlterUniqueTogether(
            name='layerversiondependency',
            unique_together=set([('layer_source', 'up_id')]),
        ),
        migrations.AlterUniqueTogether(
            name='layer_version',
            unique_together=set([('layer_source', 'up_id')]),
        ),
        migrations.AlterUniqueTogether(
            name='layer',
            unique_together=set([('layer_source', 'up_id'), ('layer_source', 'name')]),
        ),
        migrations.AlterUniqueTogether(
            name='branch',
            unique_together=set([('layer_source', 'up_id'), ('layer_source', 'name')]),
        ),
    ]
