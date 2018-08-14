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

from django.test import TestCase
from django.core import management

from orm.models import Layer_Version, Layer, Release, ToasterSetting


class TestLoadDataFixtures(TestCase):
    """ Test loading our 3 provided fixtures """
    def test_run_loaddata_poky_command(self):
        management.call_command('loaddata', 'poky')

        num_releases = Release.objects.count()

        self.assertTrue(
            Layer_Version.objects.filter(
                layer__name="meta-poky").count() == num_releases,
            "Loaded poky fixture but don't have a meta-poky for all releases"
            " defined")

    def test_run_loaddata_oecore_command(self):
        management.call_command('loaddata', 'oe-core')

        # We only have the one layer for oe-core setup
        self.assertTrue(
            Layer.objects.filter(name="openembedded-core").count() > 0,
            "Loaded oe-core fixture but still have no openemebedded-core"
            " layer")

    def test_run_loaddata_settings_command(self):
        management.call_command('loaddata', 'settings')

        self.assertTrue(
            ToasterSetting.objects.filter(name="DEFAULT_RELEASE").count() > 0,
            "Loaded settings but have no DEFAULT_RELEASE")

        self.assertTrue(
            ToasterSetting.objects.filter(
                name__startswith="DEFCONF").count() > 0,
            "Loaded settings but have no DEFCONF (default project "
            "configuration values)")
