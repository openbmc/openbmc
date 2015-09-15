# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'BuildEnvironment'
        db.create_table(u'bldcontrol_buildenvironment', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('address', self.gf('django.db.models.fields.CharField')(max_length=254)),
            ('betype', self.gf('django.db.models.fields.IntegerField')()),
            ('bbaddress', self.gf('django.db.models.fields.CharField')(max_length=254, blank=True)),
            ('bbport', self.gf('django.db.models.fields.IntegerField')(default=-1)),
            ('bbtoken', self.gf('django.db.models.fields.CharField')(max_length=126, blank=True)),
            ('bbstate', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('lock', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('created', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('updated', self.gf('django.db.models.fields.DateTimeField')(auto_now=True, blank=True)),
        ))
        db.send_create_signal(u'bldcontrol', ['BuildEnvironment'])

        # Adding model 'BuildRequest'
        db.create_table(u'bldcontrol_buildrequest', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('project', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Project'])),
            ('build', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['orm.Build'], null=True)),
            ('state', self.gf('django.db.models.fields.IntegerField')(default=0)),
            ('created', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('updated', self.gf('django.db.models.fields.DateTimeField')(auto_now=True, blank=True)),
        ))
        db.send_create_signal(u'bldcontrol', ['BuildRequest'])

        # Adding model 'BRLayer'
        db.create_table(u'bldcontrol_brlayer', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('req', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['bldcontrol.BuildRequest'])),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('giturl', self.gf('django.db.models.fields.CharField')(max_length=254)),
            ('commit', self.gf('django.db.models.fields.CharField')(max_length=254)),
        ))
        db.send_create_signal(u'bldcontrol', ['BRLayer'])

        # Adding model 'BRVariable'
        db.create_table(u'bldcontrol_brvariable', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('req', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['bldcontrol.BuildRequest'])),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('value', self.gf('django.db.models.fields.TextField')(blank=True)),
        ))
        db.send_create_signal(u'bldcontrol', ['BRVariable'])

        # Adding model 'BRTarget'
        db.create_table(u'bldcontrol_brtarget', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('req', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['bldcontrol.BuildRequest'])),
            ('target', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('task', self.gf('django.db.models.fields.CharField')(max_length=100, null=True)),
        ))
        db.send_create_signal(u'bldcontrol', ['BRTarget'])


    def backwards(self, orm):
        # Deleting model 'BuildEnvironment'
        db.delete_table(u'bldcontrol_buildenvironment')

        # Deleting model 'BuildRequest'
        db.delete_table(u'bldcontrol_buildrequest')

        # Deleting model 'BRLayer'
        db.delete_table(u'bldcontrol_brlayer')

        # Deleting model 'BRVariable'
        db.delete_table(u'bldcontrol_brvariable')

        # Deleting model 'BRTarget'
        db.delete_table(u'bldcontrol_brtarget')


    models = {
        u'bldcontrol.brlayer': {
            'Meta': {'object_name': 'BRLayer'},
            'commit': ('django.db.models.fields.CharField', [], {'max_length': '254'}),
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
            'created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'lock': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'updated': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'})
        },
        u'bldcontrol.buildrequest': {
            'Meta': {'object_name': 'BuildRequest'},
            'build': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Build']", 'null': 'True'}),
            'created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'project': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['orm.Project']"}),
            'state': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'updated': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'})
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
            'created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'updated': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'})
        }
    }

    complete_apps = ['bldcontrol']
