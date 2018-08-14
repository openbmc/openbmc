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

from orm.models import Layer_Version, Machine, Recipe


class TestLayerIndexUpdater(TestCase):
    def test_run_lsupdates_command(self):
        # Load some release information for us to fetch from the layer index
        management.call_command('loaddata', 'poky')

        old_layers_count = Layer_Version.objects.count()
        old_recipes_count = Recipe.objects.count()
        old_machines_count = Machine.objects.count()

        # Now fetch the metadata from the layer index
        management.call_command('lsupdates')

        self.assertTrue(Layer_Version.objects.count() > old_layers_count,
                        "lsupdates ran but we still have no more layers!")
        self.assertTrue(Recipe.objects.count() > old_recipes_count,
                        "lsupdates ran but we still have no more Recipes!")
        self.assertTrue(Machine.objects.count() > old_machines_count,
                        "lsupdates ran but we still have no more Machines!")
