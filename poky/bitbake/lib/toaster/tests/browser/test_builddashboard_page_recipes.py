#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.urls import reverse
from django.utils import timezone
from tests.browser.selenium_helpers import SeleniumTestCase
from orm.models import Project, Build, Recipe, Task, Layer, Layer_Version
from orm.models import Target

class TestBuilddashboardPageRecipes(SeleniumTestCase):
    """ Test build dashboard recipes sub-page """

    def setUp(self):
        project = Project.objects.get_or_create_default_project()

        now = timezone.now()

        self.build = Build.objects.create(project=project,
                                          started_on=now,
                                          completed_on=now)

        layer = Layer.objects.create()

        layer_version = Layer_Version.objects.create(layer=layer,
            build=self.build)

        recipe = Recipe.objects.create(layer_version=layer_version)

        task = Task.objects.create(build=self.build, recipe=recipe, order=1)

        Target.objects.create(build=self.build, task=task, target='do_build')

    def test_build_recipes_columns(self):
        """
        Check that non-hideable columns of the table on the recipes sub-page
        are disabled on the edit columns dropdown.
        """
        url = reverse('recipes', args=(self.build.id,))
        self.get(url)

        self.wait_until_visible('#edit-columns-button')

        # check that options for the non-hideable columns are disabled
        non_hideable = ['name', 'version']

        for column in non_hideable:
            selector = 'input#checkbox-%s[disabled="disabled"]' % column
            self.wait_until_present(selector)
