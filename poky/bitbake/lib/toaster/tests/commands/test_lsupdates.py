#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

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
