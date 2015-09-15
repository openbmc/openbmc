# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import DataMigration
from django.db import models

class Migration(DataMigration):
    # ids that cannot be imported from BuildRequest

    def forwards(self, orm):
        REQ_COMPLETED = 3
        REQ_ARCHIVE = 6
        "Write your forwards methods here."
        # Note: Don't use "from appname.models import ModelName".
        # Use orm.ModelName to refer to models in this application,
        # and orm['appname.ModelName'] for models in other applications.
        orm.BuildRequest.objects.filter(state=REQ_COMPLETED).update(state=REQ_ARCHIVE)

    def backwards(self, orm):
        REQ_COMPLETED = 3
        REQ_ARCHIVE = 6
        "Write your backwards methods here."
        orm.BuildRequest.objects.filter(state=REQ_ARCHIVE).update(state=REQ_COMPLETED)

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
    symmetrical = True
