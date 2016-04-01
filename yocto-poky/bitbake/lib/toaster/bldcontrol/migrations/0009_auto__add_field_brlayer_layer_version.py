# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding field 'BRLayer.layer_version'
        db.add_column(u'bldcontrol_brlayer', 'layer_version',
                      self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Layer_Version'], null=True),
                      keep_default=False)


    def backwards(self, orm):
        # Deleting field 'BRLayer.layer_version'
        db.delete_column(u'bldcontrol_brlayer', 'layer_version_id')


    models = {
        u'bldcontrol.brbitbake': {
            'Meta': {'object_name': 'BRBitbake'},
            'commit': ('django.db.models.fields.CharField', [], {'max_length': '254'}),
            'dirpath': ('django.db.models.fields.CharField', [], {'max_length': '254'}),
            'giturl': ('django.db.models.fields.CharField', [], {'max_length': '254'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'req': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['bldcontrol.BuildRequest']", 'unique': 'True'})
        },
        u'bldcontrol.brerror': {
            'Meta': {'object_name': 'BRError'},
            'errmsg': ('django.db.models.fields.TextField', [], {}),
            'errtype': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'req': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['bldcontrol.BuildRequest']"}),
            'traceback': ('django.db.models.fields.TextField', [], {})
        },
        u'bldcontrol.brlayer': {
            'Meta': {'object_name': 'BRLayer'},
            'commit': ('django.db.models.fields.CharField', [], {'max_length': '254'}),
            'dirpath': ('django.db.models.fields.CharField', [], {'max_length': '254'}),
            'giturl': ('django.db.models.fields.CharField', [], {'max_length': '254'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Layer_Version']", 'null': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'req': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['bldcontrol.BuildRequest']"})
        },
        u'bldcontrol.brtarget': {
            'Meta': {'object_name': 'BRTarget'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'req': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['bldcontrol.BuildRequest']"}),
            'target': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'task': ('django.db.models.fields.CharField', [], {'max_length': '100', 'null': 'True'})
        },
        u'bldcontrol.brvariable': {
            'Meta': {'object_name': 'BRVariable'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'req': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['bldcontrol.BuildRequest']"}),
            'value': ('django.db.models.fields.TextField', [], {'blank': 'True'})
        },
        u'bldcontrol.buildenvironment': {
            'Meta': {'object_name': 'BuildEnvironment'},
            'address': ('django.db.models.fields.CharField', [], {'max_length': '254'}),
            'bbaddress': ('django.db.models.fields.CharField', [], {'max_length': '254', 'blank': 'True'}),
            'bbport': ('django.db.models.fields.IntegerField', [], {'default': '-1'}),
            'bbstate': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'bbtoken': ('django.db.models.fields.CharField', [], {'max_length': '126', 'blank': 'True'}),
            'betype': ('django.db.models.fields.IntegerField', [], {}),
            'builddir': ('django.db.models.fields.CharField', [], {'max_length': '512', 'blank': 'True'}),
            'created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'lock': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'sourcedir': ('django.db.models.fields.CharField', [], {'max_length': '512', 'blank': 'True'}),
            'updated': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'})
        },
        u'bldcontrol.buildrequest': {
            'Meta': {'object_name': 'BuildRequest'},
            'build': ('django.db.models.fields.related.OneToOneField', [], {'to': u"orm['orm.Build']", 'unique': 'True', 'null': 'True'}),
            'created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'environment': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['bldcontrol.BuildEnvironment']", 'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'project': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Project']"}),
            'state': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'updated': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'})
        },
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
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'machine': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'outcome': ('django.db.models.fields.IntegerField', [], {'default': '2'}),
            'project': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Project']"}),
            'started_on': ('django.db.models.fields.DateTimeField', [], {})
        },
        u'orm.layer': {
            'Meta': {'unique_together': "(('layer_source', 'up_id'), ('layer_source', 'name'))", 'object_name': 'Layer'},
            'description': ('django.db.models.fields.TextField', [], {'default': 'None', 'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'layer_index_url': ('django.db.models.fields.URLField', [], {'max_length': '200'}),
            'layer_source': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.LayerSource']", 'null': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'summary': ('django.db.models.fields.TextField', [], {'default': 'None', 'null': 'True'}),
            'up_date': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            'up_id': ('django.db.models.fields.IntegerField', [], {'default': 'None', 'null': 'True'}),
            'vcs_url': ('django.db.models.fields.URLField', [], {'default': 'None', 'max_length': '200', 'null': 'True'}),
            'vcs_web_file_base_url': ('django.db.models.fields.URLField', [], {'default': 'None', 'max_length': '200', 'null': 'True'}),
            'vcs_web_tree_base_url': ('django.db.models.fields.URLField', [], {'default': 'None', 'max_length': '200', 'null': 'True'}),
            'vcs_web_url': ('django.db.models.fields.URLField', [], {'default': 'None', 'max_length': '200', 'null': 'True'})
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
            'local_path': ('django.db.models.fields.FilePathField', [], {'default': "'/'", 'max_length': '1024'}),
            'priority': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'project': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.Project']", 'null': 'True'}),
            'up_branch': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['orm.Branch']", 'null': 'True'}),
            'up_date': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            'up_id': ('django.db.models.fields.IntegerField', [], {'default': 'None', 'null': 'True'})
        },
        u'orm.layersource': {
            'Meta': {'unique_together': "(('sourcetype', 'apiurl'),)", 'object_name': 'LayerSource'},
            'apiurl': ('django.db.models.fields.CharField', [], {'default': 'None', 'max_length': '255', 'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '63'}),
            'sourcetype': ('django.db.models.fields.IntegerField', [], {})
        },
        u'orm.project': {
            'Meta': {'object_name': 'Project'},
            'bitbake_version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.BitbakeVersion']", 'null': 'True'}),
            'created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_default': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'release': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Release']", 'null': 'True'}),
            'short_description': ('django.db.models.fields.CharField', [], {'max_length': '50', 'blank': 'True'}),
            'updated': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'}),
            'user_id': ('django.db.models.fields.IntegerField', [], {'null': 'True'})
        },
        u'orm.release': {
            'Meta': {'object_name': 'Release'},
            'bitbake_version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.BitbakeVersion']"}),
            'branch_name': ('django.db.models.fields.CharField', [], {'default': "''", 'max_length': '50'}),
            'description': ('django.db.models.fields.CharField', [], {'max_length': '255'}),
            'helptext': ('django.db.models.fields.TextField', [], {'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '32'})
        }
    }

    complete_apps = ['bldcontrol']