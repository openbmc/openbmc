#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Copyright (C) 2013-2016 Intel Corporation
#

from django.core.urlresolvers import reverse
from django.utils import timezone
from tests.browser.selenium_helpers import SeleniumTestCase
from tests.browser.selenium_helpers_base import Wait
from orm.models import Project, Build, Task, Recipe, Layer, Layer_Version
from bldcontrol.models import BuildRequest

class TestMostRecentBuildsStates(SeleniumTestCase):
    """ Test states update correctly in most recent builds area """

    def _create_build_request(self):
        project = Project.objects.get_or_create_default_project()

        now = timezone.now()

        build = Build.objects.create(project=project, build_name='fakebuild',
            started_on=now, completed_on=now)

        return BuildRequest.objects.create(build=build, project=project,
            state=BuildRequest.REQ_QUEUED)

    def _create_recipe(self):
        """ Add a recipe to the database and return it """
        layer = Layer.objects.create()
        layer_version = Layer_Version.objects.create(layer=layer)
        return Recipe.objects.create(name='foo', layer_version=layer_version)

    def _check_build_states(self, build_request):
        recipes_to_parse = 10
        url = reverse('all-builds')
        self.get(url)

        build = build_request.build
        base_selector = '[data-latest-build-result="%s"] ' % build.id

        # build queued; check shown as queued
        selector = base_selector + '[data-build-state="Queued"]'
        element = self.wait_until_visible(selector)
        self.assertRegexpMatches(element.get_attribute('innerHTML'),
            'Build queued', 'build should show queued status')

        # waiting for recipes to be parsed
        build.outcome = Build.IN_PROGRESS
        build.recipes_to_parse = recipes_to_parse
        build.recipes_parsed = 0

        build_request.state = BuildRequest.REQ_INPROGRESS
        build_request.save()

        self.get(url)

        selector = base_selector + '[data-build-state="Parsing"]'
        element = self.wait_until_visible(selector)

        bar_selector = '#recipes-parsed-percentage-bar-%s' % build.id
        bar_element = element.find_element_by_css_selector(bar_selector)
        self.assertEqual(bar_element.value_of_css_property('width'), '0px',
            'recipe parse progress should be at 0')

        # recipes being parsed; check parse progress
        build.recipes_parsed = 5
        build.save()

        self.get(url)

        element = self.wait_until_visible(selector)
        bar_element = element.find_element_by_css_selector(bar_selector)
        recipe_bar_updated = lambda driver: \
            bar_element.get_attribute('style') == 'width: 50%;'
        msg = 'recipe parse progress bar should update to 50%'
        element = Wait(self.driver).until(recipe_bar_updated, msg)

        # all recipes parsed, task started, waiting for first task to finish;
        # check status is shown as "Tasks starting..."
        build.recipes_parsed = recipes_to_parse
        build.save()

        recipe = self._create_recipe()
        task1 = Task.objects.create(build=build, recipe=recipe,
            task_name='Lionel')
        task2 = Task.objects.create(build=build, recipe=recipe,
            task_name='Jeffries')

        self.get(url)

        selector = base_selector + '[data-build-state="Starting"]'
        element = self.wait_until_visible(selector)
        self.assertRegexpMatches(element.get_attribute('innerHTML'),
            'Tasks starting', 'build should show "tasks starting" status')

        # first task finished; check tasks progress bar
        task1.order = 1
        task1.save()

        self.get(url)

        selector = base_selector + '[data-build-state="In Progress"]'
        element = self.wait_until_visible(selector)

        bar_selector = '#build-pc-done-bar-%s' % build.id
        bar_element = element.find_element_by_css_selector(bar_selector)

        task_bar_updated = lambda driver: \
            bar_element.get_attribute('style') == 'width: 50%;'
        msg = 'tasks progress bar should update to 50%'
        element = Wait(self.driver).until(task_bar_updated, msg)

        # last task finished; check tasks progress bar updates
        task2.order = 2
        task2.save()

        self.get(url)

        element = self.wait_until_visible(selector)
        bar_element = element.find_element_by_css_selector(bar_selector)
        task_bar_updated = lambda driver: \
            bar_element.get_attribute('style') == 'width: 100%;'
        msg = 'tasks progress bar should update to 100%'
        element = Wait(self.driver).until(task_bar_updated, msg)

    def test_states_to_success(self):
        """
        Test state transitions in the recent builds area for a build which
        completes successfully.
        """
        build_request = self._create_build_request()

        self._check_build_states(build_request)

        # all tasks complete and build succeeded; check success state shown
        build = build_request.build
        build.outcome = Build.SUCCEEDED
        build.save()

        selector = '[data-latest-build-result="%s"] ' \
            '[data-build-state="Succeeded"]' % build.id
        element = self.wait_until_visible(selector)

    def test_states_to_failure(self):
        """
        Test state transitions in the recent builds area for a build which
        completes in a failure.
        """
        build_request = self._create_build_request()

        self._check_build_states(build_request)

        # all tasks complete and build succeeded; check fail state shown
        build = build_request.build
        build.outcome = Build.FAILED
        build.save()

        selector = '[data-latest-build-result="%s"] ' \
            '[data-build-state="Failed"]' % build.id
        element = self.wait_until_visible(selector)

    def test_states_cancelling(self):
        """
        Test that most recent build area updates correctly for a build
        which is cancelled.
        """
        url = reverse('all-builds')

        build_request = self._create_build_request()
        build = build_request.build

        # cancel the build
        build_request.state = BuildRequest.REQ_CANCELLING
        build_request.save()

        self.get(url)

        # check cancelling state
        selector = '[data-latest-build-result="%s"] ' \
            '[data-build-state="Cancelling"]' % build.id
        element = self.wait_until_visible(selector)
        self.assertRegexpMatches(element.get_attribute('innerHTML'),
            'Cancelling the build', 'build should show "cancelling" status')

        # check cancelled state
        build.outcome = Build.CANCELLED
        build.save()

        self.get(url)

        selector = '[data-latest-build-result="%s"] ' \
            '[data-build-state="Cancelled"]' % build.id
        element = self.wait_until_visible(selector)
        self.assertRegexpMatches(element.get_attribute('innerHTML'),
            'Build cancelled', 'build should show "cancelled" status')
