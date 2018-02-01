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

from orm.models import Project, Release, ProjectTarget, Build, ProjectVariable
from bldcontrol.models import BuildEnvironment

from bldcontrol.management.commands.runbuilds import Command\
    as RunBuildsCommand

from django.core.management import call_command

import subprocess
import logging

logger = logging.getLogger("toaster")

# We use unittest.TestCase instead of django.test.TestCase because we don't
# want to wrap everything in a database transaction as an external process
# (bitbake needs access to the database)

def load_build_environment():
    call_command('loaddata', 'settings.xml', app_label="orm")
    call_command('loaddata', 'poky.xml', app_label="orm")

    current_builddir = os.environ.get("BUILDDIR")
    if current_builddir:
        BuildTest.BUILDDIR = current_builddir
    else:
        # Setup a builddir based on default layout
        # bitbake inside openebedded-core
        oe_init_build_env_path = os.path.join(
            os.path.dirname(os.path.abspath(__file__)),
            os.pardir,
            os.pardir,
            os.pardir,
            os.pardir,
            os.pardir,
            'oe-init-build-env'
        )
        if not os.path.exists(oe_init_build_env_path):
            raise Exception("We had no BUILDDIR set and couldn't "
                            "find oe-init-build-env to set this up "
                            "ourselves please run oe-init-build-env "
                            "before running these tests")

        oe_init_build_env_path = os.path.realpath(oe_init_build_env_path)
        cmd = "bash -c 'source oe-init-build-env %s'" % BuildTest.BUILDDIR
        p = subprocess.Popen(
            cmd,
            cwd=os.path.dirname(oe_init_build_env_path),
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE)

        output, err = p.communicate()
        p.wait()

        logger.info("oe-init-build-env %s %s" % (output, err))

        os.environ['BUILDDIR'] = BuildTest.BUILDDIR

    # Setup the path to bitbake we know where to find this
    bitbake_path = os.path.join(
        os.path.dirname(os.path.abspath(__file__)),
        os.pardir,
        os.pardir,
        os.pardir,
        os.pardir,
        'bin',
        'bitbake')
    if not os.path.exists(bitbake_path):
        raise Exception("Could not find bitbake at the expected path %s"
                        % bitbake_path)

    os.environ['BBBASEDIR'] = bitbake_path

class BuildTest(unittest.TestCase):

    PROJECT_NAME = "Testbuild"
    BUILDDIR = "/tmp/build/"

    def build(self, target):
        # So that the buildinfo helper uses the test database'
        self.assertEqual(
            os.environ.get('DJANGO_SETTINGS_MODULE', ''),
            'toastermain.settings_test',
            "Please initialise django with the tests settings:  "
            "DJANGO_SETTINGS_MODULE='toastermain.settings_test'")

        built = self.target_already_built(target)
        if built:
            return built

        load_build_environment()

        BuildEnvironment.objects.get_or_create(
            betype=BuildEnvironment.TYPE_LOCAL,
            sourcedir=BuildTest.BUILDDIR,
            builddir=BuildTest.BUILDDIR
        )

        release = Release.objects.get(name='local')

        # Create a project for this build to run in
        project = Project.objects.create_project(name=BuildTest.PROJECT_NAME,
                                                 release=release)

        if os.environ.get("TOASTER_TEST_USE_SSTATE_MIRROR"):
            ProjectVariable.objects.get_or_create(
                name="SSTATE_MIRRORS",
                value="file://.* http://autobuilder.yoctoproject.org/pub/sstate/PATH;downloadfilename=PATH",
                project=project)

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

        self.assertEqual(Build.objects.get(pk=build_pk).outcome,
                         Build.SUCCEEDED,
                         "Build did not SUCCEEDED")

        logger.info("\nBuild finished %s" % build_request.build.outcome)
        return build_request.build

    def target_already_built(self, target):
        """ If the target is already built no need to build it again"""
        for build in Build.objects.filter(
                project__name=BuildTest.PROJECT_NAME):
            targets = build.target_set.values_list('target', flat=True)
            if target in targets:
                return build

        return None
