# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'Build'
        db.create_table(u'orm_build', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('machine', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('image_fstypes', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('distro', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('distro_version', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('started_on', self.gf('django.db.models.fields.DateTimeField')()),
            ('completed_on', self.gf('django.db.models.fields.DateTimeField')()),
            ('outcome', self.gf('django.db.models.fields.IntegerField')(default=2)),
            ('errors_no', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('warnings_no', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('cooker_log_path', self.gf('django.db.models.fields.CharField')(max_length=500)),
            ('build_name', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('bitbake_version', self.gf('django.db.models.fields.CharField')(max_length=50)),
        ))
        db.send_create_signal(u'orm', ['Build'])

        # Adding model 'Target'
        db.create_table(u'orm_target', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('build', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Build'])),
            ('target', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('is_image', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('file_name', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('file_size', self.gf('django.db.models.fields.IntegerField')()),
        ))
        db.send_create_signal(u'orm', ['Target'])

        # Adding model 'Task'
        db.create_table(u'orm_task', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('build', self.gf('django.db.models.fields.related.ForeignKey')(related_name='task_build', to=orm['orm.Build'])),
            ('order', self.gf('django.db.models.fields.IntegerField')(null=True)),
            ('task_executed', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('outcome', self.gf('django.db.models.fields.IntegerField')(default=5)),
            ('sstate_checksum', self.gf('django.db.models.fields.CharField')(max_length=100, blank=True)),
            ('path_to_sstate_obj', self.gf('django.db.models.fields.FilePathField')(max_length=500, blank=True)),
            ('recipe', self.gf('django.db.models.fields.related.ForeignKey')(related_name='build_recipe', to=orm['orm.Recipe'])),
            ('task_name', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('source_url', self.gf('django.db.models.fields.FilePathField')(max_length=255, blank=True)),
            ('work_directory', self.gf('django.db.models.fields.FilePathField')(max_length=255, blank=True)),
            ('script_type', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('line_number', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('disk_io', self.gf('django.db.models.fields.IntegerField')(null=True)),
            ('cpu_usage', self.gf('django.db.models.fields.DecimalField')(null=True, max_digits=6, decimal_places=2)),
            ('elapsed_time', self.gf('django.db.models.fields.CharField')(default=0, max_length=50)),
            ('sstate_result', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('message', self.gf('django.db.models.fields.CharField')(max_length=240)),
            ('logfile', self.gf('django.db.models.fields.FilePathField')(max_length=255, blank=True)),
        ))
        db.send_create_signal(u'orm', ['Task'])

        # Adding model 'Task_Dependency'
        db.create_table(u'orm_task_dependency', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('task', self.gf('django.db.models.fields.related.ForeignKey')(related_name='task_dependencies_task', to=orm['orm.Task'])),
            ('depends_on', self.gf('django.db.models.fields.related.ForeignKey')(related_name='task_dependencies_depends', to=orm['orm.Task'])),
        ))
        db.send_create_signal(u'orm', ['Task_Dependency'])

        # Adding model 'Package'
        db.create_table(u'orm_package', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('build', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Build'])),
            ('recipe', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Recipe'], null=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('version', self.gf('django.db.models.fields.CharField')(max_length=100, blank=True)),
            ('revision', self.gf('django.db.models.fields.CharField')(max_length=32, blank=True)),
            ('summary', self.gf('django.db.models.fields.CharField')(max_length=200, blank=True)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=200, blank=True)),
            ('size', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('installed_size', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('section', self.gf('django.db.models.fields.CharField')(max_length=80, blank=True)),
            ('license', self.gf('django.db.models.fields.CharField')(max_length=80, blank=True)),
        ))
        db.send_create_signal(u'orm', ['Package'])

        # Adding model 'Package_Dependency'
        db.create_table(u'orm_package_dependency', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('package', self.gf('django.db.models.fields.related.ForeignKey')(related_name='package_dependencies_source', to=orm['orm.Package'])),
            ('depends_on', self.gf('django.db.models.fields.related.ForeignKey')(related_name='package_dependencies_target', to=orm['orm.Package'])),
            ('dep_type', self.gf('django.db.models.fields.IntegerField')()),
            ('target', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Target'], null=True)),
        ))
        db.send_create_signal(u'orm', ['Package_Dependency'])

        # Adding model 'Target_Installed_Package'
        db.create_table(u'orm_target_installed_package', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('target', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Target'])),
            ('package', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Package'])),
        ))
        db.send_create_signal(u'orm', ['Target_Installed_Package'])

        # Adding model 'Package_File'
        db.create_table(u'orm_package_file', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('package', self.gf('django.db.models.fields.related.ForeignKey')(related_name='buildfilelist_package', to=orm['orm.Package'])),
            ('path', self.gf('django.db.models.fields.FilePathField')(max_length=255, blank=True)),
            ('size', self.gf('django.db.models.fields.IntegerField')()),
        ))
        db.send_create_signal(u'orm', ['Package_File'])

        # Adding model 'Recipe'
        db.create_table(u'orm_recipe', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=100, blank=True)),
            ('version', self.gf('django.db.models.fields.CharField')(max_length=100, blank=True)),
            ('layer_version', self.gf('django.db.models.fields.related.ForeignKey')(related_name='recipe_layer_version', to=orm['orm.Layer_Version'])),
            ('summary', self.gf('django.db.models.fields.CharField')(max_length=100, blank=True)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=100, blank=True)),
            ('section', self.gf('django.db.models.fields.CharField')(max_length=100, blank=True)),
            ('license', self.gf('django.db.models.fields.CharField')(max_length=200, blank=True)),
            ('licensing_info', self.gf('django.db.models.fields.TextField')(blank=True)),
            ('homepage', self.gf('django.db.models.fields.URLField')(max_length=200, blank=True)),
            ('bugtracker', self.gf('django.db.models.fields.URLField')(max_length=200, blank=True)),
            ('file_path', self.gf('django.db.models.fields.FilePathField')(max_length=255)),
        ))
        db.send_create_signal(u'orm', ['Recipe'])

        # Adding model 'Recipe_Dependency'
        db.create_table(u'orm_recipe_dependency', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('recipe', self.gf('django.db.models.fields.related.ForeignKey')(related_name='r_dependencies_recipe', to=orm['orm.Recipe'])),
            ('depends_on', self.gf('django.db.models.fields.related.ForeignKey')(related_name='r_dependencies_depends', to=orm['orm.Recipe'])),
            ('dep_type', self.gf('django.db.models.fields.IntegerField')()),
        ))
        db.send_create_signal(u'orm', ['Recipe_Dependency'])

        # Adding model 'Layer'
        db.create_table(u'orm_layer', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('local_path', self.gf('django.db.models.fields.FilePathField')(max_length=255)),
            ('layer_index_url', self.gf('django.db.models.fields.URLField')(max_length=200)),
        ))
        db.send_create_signal(u'orm', ['Layer'])

        # Adding model 'Layer_Version'
        db.create_table(u'orm_layer_version', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('build', self.gf('django.db.models.fields.related.ForeignKey')(related_name='layer_version_build', to=orm['orm.Build'])),
            ('layer', self.gf('django.db.models.fields.related.ForeignKey')(related_name='layer_version_layer', to=orm['orm.Layer'])),
            ('branch', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('commit', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('priority', self.gf('django.db.models.fields.IntegerField')()),
        ))
        db.send_create_signal(u'orm', ['Layer_Version'])

        # Adding model 'Variable'
        db.create_table(u'orm_variable', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('build', self.gf('django.db.models.fields.related.ForeignKey')(related_name='variable_build', to=orm['orm.Build'])),
            ('variable_name', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('variable_value', self.gf('django.db.models.fields.TextField')(blank=True)),
            ('changed', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('human_readable_name', self.gf('django.db.models.fields.CharField')(max_length=200)),
            ('description', self.gf('django.db.models.fields.TextField')(blank=True)),
        ))
        db.send_create_signal(u'orm', ['Variable'])

        # Adding model 'VariableHistory'
        db.create_table(u'orm_variablehistory', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('variable', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Variable'])),
            ('file_name', self.gf('django.db.models.fields.FilePathField')(max_length=255)),
            ('line_number', self.gf('django.db.models.fields.IntegerField')(null=True)),
            ('operation', self.gf('django.db.models.fields.CharField')(max_length=16)),
        ))
        db.send_create_signal(u'orm', ['VariableHistory'])

        # Adding model 'LogMessage'
        db.create_table(u'orm_logmessage', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('build', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Build'])),
            ('level', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('message', self.gf('django.db.models.fields.CharField')(max_length=240)),
            ('pathname', self.gf('django.db.models.fields.FilePathField')(max_length=255, blank=True)),
            ('lineno', self.gf('django.db.models.fields.IntegerField')(null=True)),
        ))
        db.send_create_signal(u'orm', ['LogMessage'])


    def backwards(self, orm):
        # Deleting model 'Build'
        db.delete_table(u'orm_build')

        # Deleting model 'Target'
        db.delete_table(u'orm_target')

        # Deleting model 'Task'
        db.delete_table(u'orm_task')

        # Deleting model 'Task_Dependency'
        db.delete_table(u'orm_task_dependency')

        # Deleting model 'Package'
        db.delete_table(u'orm_package')

        # Deleting model 'Package_Dependency'
        db.delete_table(u'orm_package_dependency')

        # Deleting model 'Target_Installed_Package'
        db.delete_table(u'orm_target_installed_package')

        # Deleting model 'Package_File'
        db.delete_table(u'orm_package_file')

        # Deleting model 'Recipe'
        db.delete_table(u'orm_recipe')

        # Deleting model 'Recipe_Dependency'
        db.delete_table(u'orm_recipe_dependency')

        # Deleting model 'Layer'
        db.delete_table(u'orm_layer')

        # Deleting model 'Layer_Version'
        db.delete_table(u'orm_layer_version')

        # Deleting model 'Variable'
        db.delete_table(u'orm_variable')

        # Deleting model 'VariableHistory'
        db.delete_table(u'orm_variablehistory')

        # Deleting model 'LogMessage'
        db.delete_table(u'orm_logmessage')


    models = {
        u'orm.build': {
            'Meta': {'object_name': 'Build'},
            'bitbake_version': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'build_name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'completed_on': ('django.db.models.fields.DateTimeField', [], {}),
            'cooker_log_path': ('django.db.models.fields.CharField', [], {'max_length': '500'}),
            'distro': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'distro_version': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'errors_no': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'image_fstypes': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'machine': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'outcome': ('django.db.models.fields.IntegerField', [], {'default': '2'}),
            'started_on': ('django.db.models.fields.DateTimeField', [], {}),
            'warnings_no': ('django.db.models.fields.IntegerField', [], {'default': '0'})
        },
        u'orm.layer': {
            'Meta': {'object_name': 'Layer'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_index_url': ('django.db.models.fields.URLField', [], {'max_length': '200'}),
            'local_path': ('django.db.models.fields.FilePathField', [], {'max_length': '255'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        },
        u'orm.layer_version': {
            'Meta': {'object_name': 'Layer_Version'},
            'branch': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'build': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'layer_version_build'", 'to': u"orm['orm.Build']"}),
            'commit': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'layer_version_layer'", 'to': u"orm['orm.Layer']"}),
            'priority': ('django.db.models.fields.IntegerField', [], {})
        },
        u'orm.logmessage': {
            'Meta': {'object_name': 'LogMessage'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Build']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'level': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'lineno': ('django.db.models.fields.IntegerField', [], {'null': 'True'}),
            'message': ('django.db.models.fields.CharField', [], {'max_length': '240'}),
            'pathname': ('django.db.models.fields.FilePathField', [], {'max_length': '255', 'blank': 'True'})
        },
        u'orm.package': {
            'Meta': {'object_name': 'Package'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Build']"}),
            'description': ('django.db.models.fields.CharField', [], {'max_length': '200', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'installed_size': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'license': ('django.db.models.fields.CharField', [], {'max_length': '80', 'blank': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Recipe']", 'null': 'True'}),
            'revision': ('django.db.models.fields.CharField', [], {'max_length': '32', 'blank': 'True'}),
            'section': ('django.db.models.fields.CharField', [], {'max_length': '80', 'blank': 'True'}),
            'size': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'summary': ('django.db.models.fields.CharField', [], {'max_length': '200', 'blank': 'True'}),
            'version': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'})
        },
        u'orm.package_dependency': {
            'Meta': {'object_name': 'Package_Dependency'},
            'dep_type': ('django.db.models.fields.IntegerField', [], {}),
            'depends_on': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'package_dependencies_target'", 'to': u"orm['orm.Package']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'package': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'package_dependencies_source'", 'to': u"orm['orm.Package']"}),
            'target': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Target']", 'null': 'True'})
        },
        u'orm.package_file': {
            'Meta': {'object_name': 'Package_File'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'package': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'buildfilelist_package'", 'to': u"orm['orm.Package']"}),
            'path': ('django.db.models.fields.FilePathField', [], {'max_length': '255', 'blank': 'True'}),
            'size': ('django.db.models.fields.IntegerField', [], {})
        },
        u'orm.recipe': {
            'Meta': {'object_name': 'Recipe'},
            'bugtracker': ('django.db.models.fields.URLField', [], {'max_length': '200', 'blank': 'True'}),
            'description': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'}),
            'file_path': ('django.db.models.fields.FilePathField', [], {'max_length': '255'}),
            'homepage': ('django.db.models.fields.URLField', [], {'max_length': '200', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_version': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'recipe_layer_version'", 'to': u"orm['orm.Layer_Version']"}),
            'license': ('django.db.models.fields.CharField', [], {'max_length': '200', 'blank': 'True'}),
            'licensing_info': ('django.db.models.fields.TextField', [], {'blank': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'}),
            'section': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'}),
            'summary': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'}),
            'version': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'})
        },
        u'orm.recipe_dependency': {
            'Meta': {'object_name': 'Recipe_Dependency'},
            'dep_type': ('django.db.models.fields.IntegerField', [], {}),
            'depends_on': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'r_dependencies_depends'", 'to': u"orm['orm.Recipe']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'r_dependencies_recipe'", 'to': u"orm['orm.Recipe']"})
        },
        u'orm.target': {
            'Meta': {'object_name': 'Target'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Build']"}),
            'file_name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'file_size': ('django.db.models.fields.IntegerField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_image': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'target': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        },
        u'orm.target_installed_package': {
            'Meta': {'object_name': 'Target_Installed_Package'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'package': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Package']"}),
            'target': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Target']"})
        },
        u'orm.task': {
            'Meta': {'ordering': "('order', 'recipe')", 'object_name': 'Task'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'task_build'", 'to': u"orm['orm.Build']"}),
            'cpu_usage': ('django.db.models.fields.DecimalField', [], {'null': 'True', 'max_digits': '6', 'decimal_places': '2'}),
            'disk_io': ('django.db.models.fields.IntegerField', [], {'null': 'True'}),
            'elapsed_time': ('django.db.models.fields.CharField', [], {'default': '0', 'max_length': '50'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'line_number': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'logfile': ('django.db.models.fields.FilePathField', [], {'max_length': '255', 'blank': 'True'}),
            'message': ('django.db.models.fields.CharField', [], {'max_length': '240'}),
            'order': ('django.db.models.fields.IntegerField', [], {'null': 'True'}),
            'outcome': ('django.db.models.fields.IntegerField', [], {'default': '5'}),
            'path_to_sstate_obj': ('django.db.models.fields.FilePathField', [], {'max_length': '500', 'blank': 'True'}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'build_recipe'", 'to': u"orm['orm.Recipe']"}),
            'script_type': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'source_url': ('django.db.models.fields.FilePathField', [], {'max_length': '255', 'blank': 'True'}),
            'sstate_checksum': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'}),
            'sstate_result': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'task_executed': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'task_name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'work_directory': ('django.db.models.fields.FilePathField', [], {'max_length': '255', 'blank': 'True'})
        },
        u'orm.task_dependency': {
            'Meta': {'object_name': 'Task_Dependency'},
            'depends_on': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'task_dependencies_depends'", 'to': u"orm['orm.Task']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'task': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'task_dependencies_task'", 'to': u"orm['orm.Task']"})
        },
        u'orm.variable': {
            'Meta': {'object_name': 'Variable'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'variable_build'", 'to': u"orm['orm.Build']"}),
            'changed': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'description': ('django.db.models.fields.TextField', [], {'blank': 'True'}),
            'human_readable_name': ('django.db.models.fields.CharField', [], {'max_length': '200'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'variable_name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'variable_value': ('django.db.models.fields.TextField', [], {'blank': 'True'})
        },
        u'orm.variablehistory': {
            'Meta': {'object_name': 'VariableHistory'},
            'file_name': ('django.db.models.fields.FilePathField', [], {'max_length': '255'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'line_number': ('django.db.models.fields.IntegerField', [], {'null': 'True'}),
            'operation': ('django.db.models.fields.CharField', [], {'max_length': '16'}),
            'variable': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Variable']"})
        }
    }

    complete_apps = ['orm']
