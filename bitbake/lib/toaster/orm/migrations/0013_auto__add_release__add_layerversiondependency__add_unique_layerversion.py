# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'Release'
        db.create_table(u'orm_release', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(unique=True, max_length=32)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=255)),
            ('bitbake_version', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.BitbakeVersion'])),
            ('branch', self.gf('django.db.models.fields.CharField')(max_length=32)),
        ))
        db.send_create_signal(u'orm', ['Release'])

        # Adding model 'LayerVersionDependency'
        db.create_table(u'orm_layerversiondependency', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('layer_source', self.gf('django.db.models.fields.related.ForeignKey')(default=None, to=orm['orm.LayerSource'], null=True)),
            ('up_id', self.gf('django.db.models.fields.IntegerField')(default=None, null=True)),
            ('layer_version', self.gf('django.db.models.fields.related.ForeignKey')(related_name='dependencies', to=orm['orm.Layer_Version'])),
            ('depends_on', self.gf('django.db.models.fields.related.ForeignKey')(related_name='dependees', to=orm['orm.Layer_Version'])),
        ))
        db.send_create_signal(u'orm', ['LayerVersionDependency'])

        # Adding unique constraint on 'LayerVersionDependency', fields ['layer_source', 'up_id']
        db.create_unique(u'orm_layerversiondependency', ['layer_source_id', 'up_id'])

        # Adding model 'ToasterSetting'
        db.create_table(u'orm_toastersetting', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=63)),
            ('helptext', self.gf('django.db.models.fields.TextField')()),
            ('value', self.gf('django.db.models.fields.CharField')(max_length=255)),
        ))
        db.send_create_signal(u'orm', ['ToasterSetting'])

        # Adding model 'Machine'
        db.create_table(u'orm_machine', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('layer_source', self.gf('django.db.models.fields.related.ForeignKey')(default=None, to=orm['orm.LayerSource'], null=True)),
            ('up_id', self.gf('django.db.models.fields.IntegerField')(default=None, null=True)),
            ('up_date', self.gf('django.db.models.fields.DateTimeField')(default=None, null=True)),
            ('layer_version', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Layer_Version'])),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=255)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=255)),
        ))
        db.send_create_signal(u'orm', ['Machine'])

        # Adding unique constraint on 'Machine', fields ['layer_source', 'up_id']
        db.create_unique(u'orm_machine', ['layer_source_id', 'up_id'])

        # Adding model 'ReleaseDefaultLayer'
        db.create_table(u'orm_releasedefaultlayer', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('release', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Release'])),
            ('layer', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Layer'])),
        ))
        db.send_create_signal(u'orm', ['ReleaseDefaultLayer'])

        # Adding model 'BitbakeVersion'
        db.create_table(u'orm_bitbakeversion', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(unique=True, max_length=32)),
            ('giturl', self.gf('django.db.models.fields.URLField')(max_length=200)),
            ('branch', self.gf('django.db.models.fields.CharField')(max_length=32)),
            ('dirpath', self.gf('django.db.models.fields.CharField')(max_length=255)),
        ))
        db.send_create_signal(u'orm', ['BitbakeVersion'])

        # Adding model 'Branch'
        db.create_table(u'orm_branch', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('layer_source', self.gf('django.db.models.fields.related.ForeignKey')(default=True, to=orm['orm.LayerSource'], null=True)),
            ('up_id', self.gf('django.db.models.fields.IntegerField')(default=None, null=True)),
            ('up_date', self.gf('django.db.models.fields.DateTimeField')(default=None, null=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('bitbake_branch', self.gf('django.db.models.fields.CharField')(max_length=50, blank=True)),
            ('short_description', self.gf('django.db.models.fields.CharField')(max_length=50, blank=True)),
        ))
        db.send_create_signal(u'orm', ['Branch'])

        # Adding unique constraint on 'Branch', fields ['layer_source', 'name']
        db.create_unique(u'orm_branch', ['layer_source_id', 'name'])

        # Adding unique constraint on 'Branch', fields ['layer_source', 'up_id']
        db.create_unique(u'orm_branch', ['layer_source_id', 'up_id'])

        # Adding model 'ToasterSettingDefaultLayer'
        db.create_table(u'orm_toastersettingdefaultlayer', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('layer_version', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Layer_Version'])),
        ))
        db.send_create_signal(u'orm', ['ToasterSettingDefaultLayer'])

        # Adding model 'LayerSource'
        db.create_table(u'orm_layersource', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=63)),
            ('sourcetype', self.gf('django.db.models.fields.IntegerField')()),
            ('apiurl', self.gf('django.db.models.fields.CharField')(default=None, max_length=255, null=True)),
        ))
        db.send_create_signal(u'orm', ['LayerSource'])

        # Adding unique constraint on 'LayerSource', fields ['sourcetype', 'apiurl']
        db.create_unique(u'orm_layersource', ['sourcetype', 'apiurl'])

        # Deleting field 'ProjectLayer.name'
        db.delete_column(u'orm_projectlayer', 'name')

        # Deleting field 'ProjectLayer.dirpath'
        db.delete_column(u'orm_projectlayer', 'dirpath')

        # Deleting field 'ProjectLayer.commit'
        db.delete_column(u'orm_projectlayer', 'commit')

        # Deleting field 'ProjectLayer.giturl'
        db.delete_column(u'orm_projectlayer', 'giturl')

        # Adding field 'ProjectLayer.layercommit'
        db.add_column(u'orm_projectlayer', 'layercommit',
                      self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Layer_Version'], null=True),
                      keep_default=False)

        # Adding field 'Layer_Version.layer_source'
        db.add_column(u'orm_layer_version', 'layer_source',
                      self.gf('django.db.models.fields.related.ForeignKey')(default=None, to=orm['orm.LayerSource'], null=True),
                      keep_default=False)

        # Adding field 'Layer_Version.up_id'
        db.add_column(u'orm_layer_version', 'up_id',
                      self.gf('django.db.models.fields.IntegerField')(default=None, null=True),
                      keep_default=False)

        # Adding field 'Layer_Version.up_date'
        db.add_column(u'orm_layer_version', 'up_date',
                      self.gf('django.db.models.fields.DateTimeField')(default=None, null=True),
                      keep_default=False)

        # Adding field 'Layer_Version.up_branch'
        db.add_column(u'orm_layer_version', 'up_branch',
                      self.gf('django.db.models.fields.related.ForeignKey')(default=None, to=orm['orm.Branch'], null=True),
                      keep_default=False)

        # Adding field 'Layer_Version.dirpath'
        db.add_column(u'orm_layer_version', 'dirpath',
                      self.gf('django.db.models.fields.CharField')(default=None, max_length=255, null=True),
                      keep_default=False)


        # Changing field 'Layer_Version.build'
        db.alter_column(u'orm_layer_version', 'build_id', self.gf('django.db.models.fields.related.ForeignKey')(null=True, to=orm['orm.Build']))

        # Changing field 'Layer_Version.branch'
        db.alter_column(u'orm_layer_version', 'branch', self.gf('django.db.models.fields.CharField')(max_length=80))
        # Adding unique constraint on 'Layer_Version', fields ['layer_source', 'up_id']
        db.create_unique(u'orm_layer_version', ['layer_source_id', 'up_id'])

        # Adding field 'Recipe.layer_source'
        db.add_column(u'orm_recipe', 'layer_source',
                      self.gf('django.db.models.fields.related.ForeignKey')(default=None, to=orm['orm.LayerSource'], null=True),
                      keep_default=False)

        # Adding field 'Recipe.up_id'
        db.add_column(u'orm_recipe', 'up_id',
                      self.gf('django.db.models.fields.IntegerField')(default=None, null=True),
                      keep_default=False)

        # Adding field 'Recipe.up_date'
        db.add_column(u'orm_recipe', 'up_date',
                      self.gf('django.db.models.fields.DateTimeField')(default=None, null=True),
                      keep_default=False)

        # Adding field 'Layer.layer_source'
        db.add_column(u'orm_layer', 'layer_source',
                      self.gf('django.db.models.fields.related.ForeignKey')(default=None, to=orm['orm.LayerSource'], null=True),
                      keep_default=False)

        # Adding field 'Layer.up_id'
        db.add_column(u'orm_layer', 'up_id',
                      self.gf('django.db.models.fields.IntegerField')(default=None, null=True),
                      keep_default=False)

        # Adding field 'Layer.up_date'
        db.add_column(u'orm_layer', 'up_date',
                      self.gf('django.db.models.fields.DateTimeField')(default=None, null=True),
                      keep_default=False)

        # Adding field 'Layer.vcs_url'
        db.add_column(u'orm_layer', 'vcs_url',
                      self.gf('django.db.models.fields.URLField')(default=None, max_length=200, null=True),
                      keep_default=False)

        # Adding field 'Layer.vcs_web_file_base_url'
        db.add_column(u'orm_layer', 'vcs_web_file_base_url',
                      self.gf('django.db.models.fields.URLField')(default=None, max_length=200, null=True),
                      keep_default=False)

        # Adding field 'Layer.summary'
        db.add_column(u'orm_layer', 'summary',
                      self.gf('django.db.models.fields.CharField')(default=None, max_length=200, null=True),
                      keep_default=False)

        # Adding field 'Layer.description'
        db.add_column(u'orm_layer', 'description',
                      self.gf('django.db.models.fields.TextField')(default=None, null=True),
                      keep_default=False)


        # Changing field 'Layer.local_path'
        db.alter_column(u'orm_layer', 'local_path', self.gf('django.db.models.fields.FilePathField')(max_length=255, null=True))
        # Adding unique constraint on 'Layer', fields ['layer_source', 'up_id']
        db.create_unique(u'orm_layer', ['layer_source_id', 'up_id'])

        # Adding unique constraint on 'Layer', fields ['layer_source', 'name']
        db.create_unique(u'orm_layer', ['layer_source_id', 'name'])

        # Deleting field 'Project.branch'
        db.delete_column(u'orm_project', 'branch')

        # Adding field 'Project.bitbake_version'
        db.add_column(u'orm_project', 'bitbake_version',
                      self.gf('django.db.models.fields.related.ForeignKey')(default=-1, to=orm['orm.BitbakeVersion']),
                      keep_default=False)

        # Adding field 'Project.release'
        db.add_column(u'orm_project', 'release',
                      self.gf('django.db.models.fields.related.ForeignKey')(default=-1, to=orm['orm.Release']),
                      keep_default=False)


    def backwards(self, orm):
        # Removing unique constraint on 'Layer', fields ['layer_source', 'name']
        db.delete_unique(u'orm_layer', ['layer_source_id', 'name'])

        # Removing unique constraint on 'Layer', fields ['layer_source', 'up_id']
        db.delete_unique(u'orm_layer', ['layer_source_id', 'up_id'])

        # Removing unique constraint on 'Layer_Version', fields ['layer_source', 'up_id']
        db.delete_unique(u'orm_layer_version', ['layer_source_id', 'up_id'])

        # Removing unique constraint on 'LayerSource', fields ['sourcetype', 'apiurl']
        db.delete_unique(u'orm_layersource', ['sourcetype', 'apiurl'])

        # Removing unique constraint on 'Branch', fields ['layer_source', 'up_id']
        db.delete_unique(u'orm_branch', ['layer_source_id', 'up_id'])

        # Removing unique constraint on 'Branch', fields ['layer_source', 'name']
        db.delete_unique(u'orm_branch', ['layer_source_id', 'name'])

        # Removing unique constraint on 'Machine', fields ['layer_source', 'up_id']
        db.delete_unique(u'orm_machine', ['layer_source_id', 'up_id'])

        # Removing unique constraint on 'LayerVersionDependency', fields ['layer_source', 'up_id']
        db.delete_unique(u'orm_layerversiondependency', ['layer_source_id', 'up_id'])

        # Deleting model 'Release'
        db.delete_table(u'orm_release')

        # Deleting model 'LayerVersionDependency'
        db.delete_table(u'orm_layerversiondependency')

        # Deleting model 'ToasterSetting'
        db.delete_table(u'orm_toastersetting')

        # Deleting model 'Machine'
        db.delete_table(u'orm_machine')

        # Deleting model 'ReleaseDefaultLayer'
        db.delete_table(u'orm_releasedefaultlayer')

        # Deleting model 'BitbakeVersion'
        db.delete_table(u'orm_bitbakeversion')

        # Deleting model 'Branch'
        db.delete_table(u'orm_branch')

        # Deleting model 'ToasterSettingDefaultLayer'
        db.delete_table(u'orm_toastersettingdefaultlayer')

        # Deleting model 'LayerSource'
        db.delete_table(u'orm_layersource')


        # User chose to not deal with backwards NULL issues for 'ProjectLayer.name'
        raise RuntimeError("Cannot reverse this migration. 'ProjectLayer.name' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration        # Adding field 'ProjectLayer.name'
        db.add_column(u'orm_projectlayer', 'name',
                      self.gf('django.db.models.fields.CharField')(max_length=100),
                      keep_default=False)


        # User chose to not deal with backwards NULL issues for 'ProjectLayer.dirpath'
        raise RuntimeError("Cannot reverse this migration. 'ProjectLayer.dirpath' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration        # Adding field 'ProjectLayer.dirpath'
        db.add_column(u'orm_projectlayer', 'dirpath',
                      self.gf('django.db.models.fields.CharField')(max_length=254),
                      keep_default=False)


        # User chose to not deal with backwards NULL issues for 'ProjectLayer.commit'
        raise RuntimeError("Cannot reverse this migration. 'ProjectLayer.commit' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration        # Adding field 'ProjectLayer.commit'
        db.add_column(u'orm_projectlayer', 'commit',
                      self.gf('django.db.models.fields.CharField')(max_length=254),
                      keep_default=False)


        # User chose to not deal with backwards NULL issues for 'ProjectLayer.giturl'
        raise RuntimeError("Cannot reverse this migration. 'ProjectLayer.giturl' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration        # Adding field 'ProjectLayer.giturl'
        db.add_column(u'orm_projectlayer', 'giturl',
                      self.gf('django.db.models.fields.CharField')(max_length=254),
                      keep_default=False)

        # Deleting field 'ProjectLayer.layercommit'
        db.delete_column(u'orm_projectlayer', 'layercommit_id')

        # Deleting field 'Layer_Version.layer_source'
        db.delete_column(u'orm_layer_version', 'layer_source_id')

        # Deleting field 'Layer_Version.up_id'
        db.delete_column(u'orm_layer_version', 'up_id')

        # Deleting field 'Layer_Version.up_date'
        db.delete_column(u'orm_layer_version', 'up_date')

        # Deleting field 'Layer_Version.up_branch'
        db.delete_column(u'orm_layer_version', 'up_branch_id')

        # Deleting field 'Layer_Version.dirpath'
        db.delete_column(u'orm_layer_version', 'dirpath')


        # User chose to not deal with backwards NULL issues for 'Layer_Version.build'
        raise RuntimeError("Cannot reverse this migration. 'Layer_Version.build' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration
        # Changing field 'Layer_Version.build'
        db.alter_column(u'orm_layer_version', 'build_id', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Build']))

        # Changing field 'Layer_Version.branch'
        db.alter_column(u'orm_layer_version', 'branch', self.gf('django.db.models.fields.CharField')(max_length=50))
        # Deleting field 'Recipe.layer_source'
        db.delete_column(u'orm_recipe', 'layer_source_id')

        # Deleting field 'Recipe.up_id'
        db.delete_column(u'orm_recipe', 'up_id')

        # Deleting field 'Recipe.up_date'
        db.delete_column(u'orm_recipe', 'up_date')

        # Deleting field 'Layer.layer_source'
        db.delete_column(u'orm_layer', 'layer_source_id')

        # Deleting field 'Layer.up_id'
        db.delete_column(u'orm_layer', 'up_id')

        # Deleting field 'Layer.up_date'
        db.delete_column(u'orm_layer', 'up_date')

        # Deleting field 'Layer.vcs_url'
        db.delete_column(u'orm_layer', 'vcs_url')

        # Deleting field 'Layer.vcs_web_file_base_url'
        db.delete_column(u'orm_layer', 'vcs_web_file_base_url')

        # Deleting field 'Layer.summary'
        db.delete_column(u'orm_layer', 'summary')

        # Deleting field 'Layer.description'
        db.delete_column(u'orm_layer', 'description')


        # User chose to not deal with backwards NULL issues for 'Layer.local_path'
        raise RuntimeError("Cannot reverse this migration. 'Layer.local_path' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration
        # Changing field 'Layer.local_path'
        db.alter_column(u'orm_layer', 'local_path', self.gf('django.db.models.fields.FilePathField')(max_length=255))

        # User chose to not deal with backwards NULL issues for 'Project.branch'
        raise RuntimeError("Cannot reverse this migration. 'Project.branch' and its values cannot be restored.")
        
        # The following code is provided here to aid in writing a correct migration        # Adding field 'Project.branch'
        db.add_column(u'orm_project', 'branch',
                      self.gf('django.db.models.fields.CharField')(max_length=50),
                      keep_default=False)

        # Deleting field 'Project.bitbake_version'
        db.delete_column(u'orm_project', 'bitbake_version_id')

        # Deleting field 'Project.release'
        db.delete_column(u'orm_project', 'release_id')


    models = {
        u'orm.bitbakeversion': {
            'Meta': {'object_name': 'BitbakeVersion'},
            'branch': ('django.db.models.fields.CharField', [], {'max_length': '32'}),
            'dirpath': ('django.db.models.fields.CharField', [], {'max_length': '255'}),
            'giturl': ('django.db.models.fields.URLField', [], {'max_length': '200'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '32'})
        },
        u'orm.branch': {
            'Meta': {'unique_together': "(('layer_source', 'name'), ('layer_source', 'up_id'))", 'object_name': 'Branch'},
            'bitbake_branch': ('django.db.models.fields.CharField', [], {'max_length': '50', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_source': ('django.db.models.fields.related.ForeignKey', [], {'default': 'True', 'to': u"orm['orm.LayerSource']", 'null': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'short_description': ('django.db.models.fields.CharField', [], {'max_length': '50', 'blank': 'True'}),
            'up_date': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            'up_id': ('django.db.models.fields.IntegerField', [], {'default': 'None', 'null': 'True'})
        },
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
            'project': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Project']", 'null': 'True'}),
            'started_on': ('django.db.models.fields.DateTimeField', [], {}),
            'timespent': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'warnings_no': ('django.db.models.fields.IntegerField', [], {'default': '0'})
        },
        u'orm.helptext': {
            'Meta': {'object_name': 'HelpText'},
            'area': ('django.db.models.fields.IntegerField', [], {}),
            'build': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'helptext_build'", 'to': u"orm['orm.Build']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'key': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'text': ('django.db.models.fields.TextField', [], {})
        },
        u'orm.layer': {
            'Meta': {'unique_together': "(('layer_source', 'up_id'), ('layer_source', 'name'))", 'object_name': 'Layer'},
            'description': ('django.db.models.fields.TextField', [], {'default': 'None', 'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_index_url': ('django.db.models.fields.URLField', [], {'max_length': '200'}),
            'layer_source': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.LayerSource']", 'null': 'True'}),
            'local_path': ('django.db.models.fields.FilePathField', [], {'default': 'None', 'max_length': '255', 'null': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'summary': ('django.db.models.fields.CharField', [], {'default': 'None', 'max_length': '200', 'null': 'True'}),
            'up_date': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            'up_id': ('django.db.models.fields.IntegerField', [], {'default': 'None', 'null': 'True'}),
            'vcs_url': ('django.db.models.fields.URLField', [], {'default': 'None', 'max_length': '200', 'null': 'True'}),
            'vcs_web_file_base_url': ('django.db.models.fields.URLField', [], {'default': 'None', 'max_length': '200', 'null': 'True'})
        },
        u'orm.layer_version': {
            'Meta': {'unique_together': "(('layer_source', 'up_id'),)", 'object_name': 'Layer_Version'},
            'branch': ('django.db.models.fields.CharField', [], {'max_length': '80'}),
            'build': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'related_name': "'layer_version_build'", 'null': 'True', 'to': u"orm['orm.Build']"}),
            'commit': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'dirpath': ('django.db.models.fields.CharField', [], {'default': 'None', 'max_length': '255', 'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'layer_version_layer'", 'to': u"orm['orm.Layer']"}),
            'layer_source': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.LayerSource']", 'null': 'True'}),
            'priority': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'up_branch': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.Branch']", 'null': 'True'}),
            'up_date': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            'up_id': ('django.db.models.fields.IntegerField', [], {'default': 'None', 'null': 'True'})
        },
        u'orm.layersource': {
            'Meta': {'unique_together': "(('sourcetype', 'apiurl'),)", 'object_name': 'LayerSource'},
            'apiurl': ('django.db.models.fields.CharField', [], {'default': 'None', 'max_length': '255', 'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '63'}),
            'sourcetype': ('django.db.models.fields.IntegerField', [], {})
        },
        u'orm.layerversiondependency': {
            'Meta': {'unique_together': "(('layer_source', 'up_id'),)", 'object_name': 'LayerVersionDependency'},
            'depends_on': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'dependees'", 'to': u"orm['orm.Layer_Version']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_source': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.LayerSource']", 'null': 'True'}),
            'layer_version': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'dependencies'", 'to': u"orm['orm.Layer_Version']"}),
            'up_id': ('django.db.models.fields.IntegerField', [], {'default': 'None', 'null': 'True'})
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
        u'orm.machine': {
            'Meta': {'unique_together': "(('layer_source', 'up_id'),)", 'object_name': 'Machine'},
            'description': ('django.db.models.fields.CharField', [], {'max_length': '255'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_source': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.LayerSource']", 'null': 'True'}),
            'layer_version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Layer_Version']"}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '255'}),
            'up_date': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            'up_id': ('django.db.models.fields.IntegerField', [], {'default': 'None', 'null': 'True'})
        },
        u'orm.package': {
            'Meta': {'object_name': 'Package'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Build']"}),
            'description': ('django.db.models.fields.TextField', [], {'blank': 'True'}),
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
        u'orm.project': {
            'Meta': {'object_name': 'Project'},
            'bitbake_version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.BitbakeVersion']"}),
            'created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'release': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Release']"}),
            'short_description': ('django.db.models.fields.CharField', [], {'max_length': '50', 'blank': 'True'}),
            'updated': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'}),
            'user_id': ('django.db.models.fields.IntegerField', [], {'null': 'True'})
        },
        u'orm.projectlayer': {
            'Meta': {'object_name': 'ProjectLayer'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layercommit': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Layer_Version']", 'null': 'True'}),
            'optional': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'project': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Project']"})
        },
        u'orm.projecttarget': {
            'Meta': {'object_name': 'ProjectTarget'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'project': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Project']"}),
            'target': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'task': ('django.db.models.fields.CharField', [], {'max_length': '100', 'null': 'True'})
        },
        u'orm.projectvariable': {
            'Meta': {'object_name': 'ProjectVariable'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'project': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Project']"}),
            'value': ('django.db.models.fields.TextField', [], {'blank': 'True'})
        },
        u'orm.recipe': {
            'Meta': {'object_name': 'Recipe'},
            'bugtracker': ('django.db.models.fields.URLField', [], {'max_length': '200', 'blank': 'True'}),
            'description': ('django.db.models.fields.TextField', [], {'blank': 'True'}),
            'file_path': ('django.db.models.fields.FilePathField', [], {'max_length': '255'}),
            'homepage': ('django.db.models.fields.URLField', [], {'max_length': '200', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_source': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.LayerSource']", 'null': 'True'}),
            'layer_version': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'recipe_layer_version'", 'to': u"orm['orm.Layer_Version']"}),
            'license': ('django.db.models.fields.CharField', [], {'max_length': '200', 'blank': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'}),
            'section': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'}),
            'summary': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'}),
            'up_date': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            'up_id': ('django.db.models.fields.IntegerField', [], {'default': 'None', 'null': 'True'}),
            'version': ('django.db.models.fields.CharField', [], {'max_length': '100', 'blank': 'True'})
        },
        u'orm.recipe_dependency': {
            'Meta': {'object_name': 'Recipe_Dependency'},
            'dep_type': ('django.db.models.fields.IntegerField', [], {}),
            'depends_on': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'r_dependencies_depends'", 'to': u"orm['orm.Recipe']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'r_dependencies_recipe'", 'to': u"orm['orm.Recipe']"})
        },
        u'orm.release': {
            'Meta': {'object_name': 'Release'},
            'bitbake_version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.BitbakeVersion']"}),
            'branch': ('django.db.models.fields.CharField', [], {'max_length': '32'}),
            'description': ('django.db.models.fields.CharField', [], {'max_length': '255'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '32'})
        },
        u'orm.releasedefaultlayer': {
            'Meta': {'object_name': 'ReleaseDefaultLayer'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Layer']"}),
            'release': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Release']"})
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
            'file_name': ('django.db.models.fields.FilePathField', [], {'max_length': '254'}),
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
        u'orm.toastersetting': {
            'Meta': {'object_name': 'ToasterSetting'},
            'helptext': ('django.db.models.fields.TextField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '63'}),
            'value': ('django.db.models.fields.CharField', [], {'max_length': '255'})
        },
        u'orm.toastersettingdefaultlayer': {
            'Meta': {'object_name': 'ToasterSettingDefaultLayer'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Layer_Version']"})
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
            'operation': ('django.db.models.fields.CharField', [], {'max_length': '64'}),
            'value': ('django.db.models.fields.TextField', [], {'blank': 'True'}),
            'variable': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'vhistory'", 'to': u"orm['orm.Variable']"})
        }
    }

    complete_apps = ['orm']