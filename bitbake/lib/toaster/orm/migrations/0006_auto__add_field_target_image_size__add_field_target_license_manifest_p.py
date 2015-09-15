# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding field 'Target.image_size'
        db.add_column(u'orm_target', 'image_size',
                      self.gf('django.db.models.fields.IntegerField')(default=0),
                      keep_default=False)

        # Adding field 'Target.license_manifest_path'
        db.add_column(u'orm_target', 'license_manifest_path',
                      self.gf('django.db.models.fields.CharField')(max_length=500, null=True),
                      keep_default=False)


        # Changing field 'Target_File.permission'
        db.alter_column(u'orm_target_file', 'permission', self.gf('django.db.models.fields.CharField')(max_length=16))

        # Changing field 'Target_File.sym_target'
        db.alter_column(u'orm_target_file', 'sym_target_id', self.gf('django.db.models.fields.related.ForeignKey')(null=True, to=orm['orm.Target_File']))

        # Changing field 'Target_File.directory'
        db.alter_column(u'orm_target_file', 'directory_id', self.gf('django.db.models.fields.related.ForeignKey')(null=True, to=orm['orm.Target_File']))

    def backwards(self, orm):
        # Deleting field 'Target.image_size'
        db.delete_column(u'orm_target', 'image_size')

        # Deleting field 'Target.license_manifest_path'
        db.delete_column(u'orm_target', 'license_manifest_path')


        # Changing field 'Target_File.permission'
        db.alter_column(u'orm_target_file', 'permission', self.gf('django.db.models.fields.IntegerField')())

        # User chose to not deal with backwards NULL issues for 'Target_File.sym_target'
        raise RuntimeError("Cannot reverse this migration. 'Target_File.sym_target' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration
        # Changing field 'Target_File.sym_target'
        db.alter_column(u'orm_target_file', 'sym_target_id', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Target_File']))

        # User chose to not deal with backwards NULL issues for 'Target_File.directory'
        raise RuntimeError("Cannot reverse this migration. 'Target_File.directory' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration
        # Changing field 'Target_File.directory'
        db.alter_column(u'orm_target_file', 'directory_id', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Target_File']))

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
            'machine': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'outcome': ('django.db.models.fields.IntegerField', [], {'default': '2'}),
            'started_on': ('django.db.models.fields.DateTimeField', [], {}),
            'timespent': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
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
            'pathname': ('django.db.models.fields.FilePathField', [], {'max_length': '255', 'blank': 'True'}),
            'task': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Task']", 'null': 'True', 'blank': 'True'})
        },
        u'orm.package': {
            'Meta': {'object_name': 'Package'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Build']"}),
            'description': ('django.db.models.fields.CharField', [], {'max_length': '200', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'installed_name': ('django.db.models.fields.CharField', [], {'default': "''", 'max_length': '100'}),
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
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'image_size': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'is_image': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'license_manifest_path': ('django.db.models.fields.CharField', [], {'max_length': '500', 'null': 'True'}),
            'target': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        },
        u'orm.target_file': {
            'Meta': {'object_name': 'Target_File'},
            'directory': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'directory_set'", 'null': 'True', 'to': u"orm['orm.Target_File']"}),
            'group': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'inodetype': ('django.db.models.fields.IntegerField', [], {}),
            'owner': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'path': ('django.db.models.fields.FilePathField', [], {'max_length': '100'}),
            'permission': ('django.db.models.fields.CharField', [], {'max_length': '16'}),
            'size': ('django.db.models.fields.IntegerField', [], {}),
            'sym_target': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'symlink_set'", 'null': 'True', 'to': u"orm['orm.Target_File']"}),
            'target': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Target']"})
        },
        u'orm.target_image_file': {
            'Meta': {'object_name': 'Target_Image_File'},
            'file_name': ('django.db.models.fields.FilePathField', [], {'max_length': '100'}),
            'file_size': ('django.db.models.fields.IntegerField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'target': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Target']"})
        },
        u'orm.target_installed_package': {
            'Meta': {'object_name': 'Target_Installed_Package'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'package': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'buildtargetlist_package'", 'to': u"orm['orm.Package']"}),
            'target': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Target']"})
        },
        u'orm.task': {
            'Meta': {'ordering': "('order', 'recipe')", 'unique_together': "(('build', 'recipe', 'task_name'),)", 'object_name': 'Task'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'task_build'", 'to': u"orm['orm.Build']"}),
            'cpu_usage': ('django.db.models.fields.DecimalField', [], {'null': 'True', 'max_digits': '6', 'decimal_places': '2'}),
            'disk_io': ('django.db.models.fields.IntegerField', [], {'null': 'True'}),
            'elapsed_time': ('django.db.models.fields.DecimalField', [], {'null': 'True', 'max_digits': '6', 'decimal_places': '2'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'line_number': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'logfile': ('django.db.models.fields.FilePathField', [], {'max_length': '255', 'blank': 'True'}),
            'message': ('django.db.models.fields.CharField', [], {'max_length': '240'}),
            'order': ('django.db.models.fields.IntegerField', [], {'null': 'True'}),
            'outcome': ('django.db.models.fields.IntegerField', [], {'default': '-1'}),
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
            'value': ('django.db.models.fields.TextField', [], {'blank': 'True'}),
            'variable': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'vhistory'", 'to': u"orm['orm.Variable']"})
        }
    }

    complete_apps = ['orm']
