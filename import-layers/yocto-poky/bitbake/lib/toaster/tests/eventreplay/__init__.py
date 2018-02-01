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

# Tests were part of openembedded-core oe selftest Authored by: Lucian Musat
# Ionut Chisanovici, Paul Eggleton and Cristian Iorga

"""
Test toaster backend by playing build event log files
using toaster-eventreplay script
"""

import os

from subprocess import getstatusoutput
from pathlib import Path

from django.test import TestCase

from orm.models import Target_Installed_Package, Package, Build

class EventReplay(TestCase):
    """Base class for eventreplay test cases"""

    def setUp(self):
        """
        Setup build environment:
            - set self.script to toaster-eventreplay path
            - set self.eventplay_dir to the value of EVENTPLAY_DIR env variable
        """
        bitbake_dir = Path(__file__.split('lib/toaster')[0])
        self.script = bitbake_dir /  'bin' / 'toaster-eventreplay'
        self.assertTrue(self.script.exists(), "%s doesn't exist")
        self.eventplay_dir = os.getenv("EVENTREPLAY_DIR")
        self.assertTrue(self.eventplay_dir,
                        "Environment variable EVENTREPLAY_DIR is not set")

    def _replay(self, eventfile):
        """Run toaster-eventplay <eventfile>"""
        eventpath = Path(self.eventplay_dir) / eventfile
        status, output = getstatusoutput('%s %s' % (self.script, eventpath))
        if status:
            print(output)

        self.assertEqual(status, 0)

class CoreImageMinimalEventReplay(EventReplay):
    """Replay core-image-minimal events"""

    def test_installed_packages(self):
        """Test if all required packages have been installed"""

        self._replay('core-image-minimal.events')

        # test installed packages
        packages = sorted(Target_Installed_Package.objects.\
                          values_list('package__name', flat=True))
        self.assertEqual(packages, ['base-files', 'base-passwd', 'busybox',
                                    'busybox-hwclock', 'busybox-syslog',
                                    'busybox-udhcpc', 'eudev', 'glibc',
                                    'init-ifupdown', 'initscripts',
                                    'initscripts-functions', 'kernel-base',
                                    'kernel-module-uvesafb', 'libkmod',
                                    'modutils-initscripts', 'netbase',
                                    'packagegroup-core-boot', 'run-postinsts',
                                    'sysvinit', 'sysvinit-inittab',
                                    'sysvinit-pidof', 'udev-cache',
                                    'update-alternatives-opkg',
                                    'update-rc.d', 'util-linux-libblkid',
                                    'util-linux-libuuid', 'v86d', 'zlib'])

class ZlibEventReplay(EventReplay):
    """Replay zlib events"""

    def test_replay_zlib(self):
        """Test if zlib build and package are in the database"""
        self._replay("zlib.events")

        self.assertEqual(Build.objects.last().target_set.last().target, "zlib")
        self.assertTrue('zlib' in Package.objects.values_list('name', flat=True))
