# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('orm', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='BRBitbake',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('giturl', models.CharField(max_length=254)),
                ('commit', models.CharField(max_length=254)),
                ('dirpath', models.CharField(max_length=254)),
            ],
        ),
        migrations.CreateModel(
            name='BRError',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('errtype', models.CharField(max_length=100)),
                ('errmsg', models.TextField()),
                ('traceback', models.TextField()),
            ],
        ),
        migrations.CreateModel(
            name='BRLayer',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=100)),
                ('giturl', models.CharField(max_length=254)),
                ('commit', models.CharField(max_length=254)),
                ('dirpath', models.CharField(max_length=254)),
                ('layer_version', models.ForeignKey(to='orm.Layer_Version', null=True, on_delete=models.CASCADE)),
            ],
        ),
        migrations.CreateModel(
            name='BRTarget',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('target', models.CharField(max_length=100)),
                ('task', models.CharField(max_length=100, null=True)),
            ],
        ),
        migrations.CreateModel(
            name='BRVariable',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=100)),
                ('value', models.TextField(blank=True)),
            ],
        ),
        migrations.CreateModel(
            name='BuildEnvironment',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('address', models.CharField(max_length=254)),
                ('betype', models.IntegerField(choices=[(0, b'local'), (1, b'ssh')])),
                ('bbaddress', models.CharField(max_length=254, blank=True)),
                ('bbport', models.IntegerField(default=-1)),
                ('bbtoken', models.CharField(max_length=126, blank=True)),
                ('bbstate', models.IntegerField(default=0, choices=[(0, b'stopped'), (1, b'started')])),
                ('sourcedir', models.CharField(max_length=512, blank=True)),
                ('builddir', models.CharField(max_length=512, blank=True)),
                ('lock', models.IntegerField(default=0, choices=[(0, b'free'), (1, b'lock'), (2, b'running')])),
                ('created', models.DateTimeField(auto_now_add=True)),
                ('updated', models.DateTimeField(auto_now=True)),
            ],
        ),
        migrations.CreateModel(
            name='BuildRequest',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('state', models.IntegerField(default=0, choices=[(0, b'created'), (1, b'queued'), (2, b'in progress'), (3, b'completed'), (4, b'failed'), (5, b'deleted'), (6, b'archive')])),
                ('created', models.DateTimeField(auto_now_add=True)),
                ('updated', models.DateTimeField(auto_now=True)),
                ('build', models.OneToOneField(null=True, to='orm.Build', on_delete=models.CASCADE)),
                ('environment', models.ForeignKey(to='bldcontrol.BuildEnvironment', null=True, on_delete=models.CASCADE)),
                ('project', models.ForeignKey(to='orm.Project', on_delete=models.CASCADE)),
            ],
        ),
        migrations.AddField(
            model_name='brvariable',
            name='req',
            field=models.ForeignKey(to='bldcontrol.BuildRequest', on_delete=models.CASCADE),
        ),
        migrations.AddField(
            model_name='brtarget',
            name='req',
            field=models.ForeignKey(to='bldcontrol.BuildRequest', on_delete=models.CASCADE),
        ),
        migrations.AddField(
            model_name='brlayer',
            name='req',
            field=models.ForeignKey(to='bldcontrol.BuildRequest', on_delete=models.CASCADE),
        ),
        migrations.AddField(
            model_name='brerror',
            name='req',
            field=models.ForeignKey(to='bldcontrol.BuildRequest', on_delete=models.CASCADE),
        ),
        migrations.AddField(
            model_name='brbitbake',
            name='req',
            field=models.OneToOneField(to='bldcontrol.BuildRequest', on_delete=models.CASCADE),
        ),
    ]
