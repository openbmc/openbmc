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
from orm.models import Project, Build, Layer, Layer_Version, Recipe, Target
from orm.models import Task, Task_Dependency

class TestTaskPage(SeleniumTestCase):
    """ Test page which shows an individual task """
    RECIPE_NAME = 'bar'
    RECIPE_VERSION = '0.1'
    TASK_NAME = 'do_da_doo_ron_ron'

    def setUp(self):
        now = timezone.now()

        project = Project.objects.get_or_create_default_project()

        self.build = Build.objects.create(project=project, started_on=now,
            completed_on=now)

        Target.objects.create(target='foo', build=self.build)

        layer = Layer.objects.create()

        layer_version = Layer_Version.objects.create(layer=layer)

        recipe = Recipe.objects.create(name=TestTaskPage.RECIPE_NAME,
            layer_version=layer_version, version=TestTaskPage.RECIPE_VERSION)

        self.task = Task.objects.create(build=self.build, recipe=recipe,
            order=1, outcome=Task.OUTCOME_COVERED, task_executed=False,
            task_name=TestTaskPage.TASK_NAME)

    def test_covered_task(self):
        """
        Check that covered tasks are displayed for tasks which have
        dependencies on themselves
        """

        # the infinite loop which of bug 9952 was down to tasks which
        # depend on themselves, so add self-dependent tasks to replicate the
        # situation which caused the infinite loop (now fixed)
        Task_Dependency.objects.create(task=self.task, depends_on=self.task)

        url = reverse('task', args=(self.build.id, self.task.id,))
        self.get(url)

        # check that we see the task name
        self.wait_until_visible('.page-header h1')

        heading = self.find('.page-header h1')
        expected_heading = '%s_%s %s' % (TestTaskPage.RECIPE_NAME,
            TestTaskPage.RECIPE_VERSION, TestTaskPage.TASK_NAME)
        self.assertEqual(heading.text, expected_heading,
            'Heading should show recipe name, version and task')
