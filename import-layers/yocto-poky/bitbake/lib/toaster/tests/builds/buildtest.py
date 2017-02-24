#! /usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2016 Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import os
import sys
import time
import unittest

from orm.models import Project, Release, ProjectTarget, Build
from bldcontrol.models import BuildEnvironment

from bldcontrol.management.commands.loadconf import Command\
    as LoadConfigCommand

from bldcontrol.management.commands.runbuilds import Command\
    as RunBuildsCommand

import subprocess

# We use unittest.TestCase instead of django.test.TestCase because we don't
# want to wrap everything in a database transaction as an external process
# (bitbake needs access to the database)


class BuildTest(unittest.TestCase):

    PROJECT_NAME = "Testbuild"

    def build(self, target):
        # So that the buildinfo helper uses the test database'
        self.assertEqual(
            os.environ.get('DJANGO_SETTINGS_MODULE', ''),
            'toastermain.settings-test',
            "Please initialise django with the tests settings:  "
            "DJANGO_SETTINGS_MODULE='toastermain.settings-test'")

        if self.target_already_built(target):
            return

        # Take a guess at the location of the toasterconf
        poky_toaster_conf = '../../../meta-poky/conf/toasterconf.json'
        oe_toaster_conf = '../../../meta/conf/toasterconf.json'
        env_toaster_conf = os.environ.get('TOASTER_CONF')

        config_file = None
        if env_toaster_conf:
            config_file = env_toaster_conf
        else:
            if os.path.exists(poky_toaster_conf):
                config_file = poky_toaster_conf
            elif os.path.exists(oe_toaster_conf):
                config_file = oe_toaster_conf

        self.assertIsNotNone(config_file,
                             "Default locations for toasterconf not found"
                             "please set $TOASTER_CONF manually")

        # Setup the release information and default layers
        print("\nImporting file: %s" % config_file)
        os.environ['TOASTER_CONF'] = config_file
        LoadConfigCommand()._import_layer_config(config_file)

        os.environ['TOASTER_DIR'] = \
            os.path.abspath(os.environ['BUILDDIR'] + "/../")

        os.environ['BBBASEDIR'] = \
            subprocess.check_output('which bitbake', shell=True)

        BuildEnvironment.objects.get_or_create(
            betype=BuildEnvironment.TYPE_LOCAL,
            sourcedir=os.environ['TOASTER_DIR'],
            builddir=os.environ['BUILDDIR']
        )

        release = Release.objects.get(name='local')

        # Create a project for this build to run in
        try:
            project = Project.objects.get(name=BuildTest.PROJECT_NAME)
        except Project.DoesNotExist:
            project = Project.objects.create_project(
                name=BuildTest.PROJECT_NAME,
                release=release
            )

        ProjectTarget.objects.create(project=project,
                                     target=target,
                                     task="")
        build_request = project.schedule_build()

        # run runbuilds command to dispatch the build
        # e.g. manage.py runubilds
        RunBuildsCommand().runbuild()

        build_pk = build_request.build.pk
        while Build.objects.get(pk=build_pk).outcome == Build.IN_PROGRESS:
            sys.stdout.write("\rBuilding %s %d%%" %
                             (target,
                              build_request.build.completeper()))
            sys.stdout.flush()
            time.sleep(1)

        self.assertNotEqual(build_request.build.outcome,
                            Build.SUCCEEDED, "Build did not SUCCEEDED")
        print("\nBuild finished")
        return build_request.build

    def target_already_built(self, target):
        """ If the target is already built no need to build it again"""
        for build in Build.objects.filter(
                project__name=BuildTest.PROJECT_NAME):
            targets = build.target_set.values_list('target', flat=True)
            if target in targets:
                return True

        return False
