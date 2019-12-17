#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re

from django.core.urlresolvers import reverse
from django.utils import timezone
from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import BitbakeVersion, Release, Project, Build, Target


class TestAllBuildsPage(SeleniumTestCase):
    """ Tests for all builds page /builds/ """

    PROJECT_NAME = 'test project'
    CLI_BUILDS_PROJECT_NAME = 'command line builds'

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name='bbv1', giturl='/tmp/',
                                            branch='master', dirpath='')
        release = Release.objects.create(name='release1',
                                         bitbake_version=bbv)
        self.project1 = Project.objects.create_project(name=self.PROJECT_NAME,
                                                       release=release)
        self.default_project = Project.objects.create_project(
            name=self.CLI_BUILDS_PROJECT_NAME,
            release=release
        )
        self.default_project.is_default = True
        self.default_project.save()

        # parameters for builds to associate with the projects
        now = timezone.now()

        self.project1_build_success = {
            'project': self.project1,
            'started_on': now,
            'completed_on': now,
            'outcome': Build.SUCCEEDED
        }

        self.project1_build_failure = {
            'project': self.project1,
            'started_on': now,
            'completed_on': now,
            'outcome': Build.FAILED
        }

        self.default_project_build_success = {
            'project': self.default_project,
            'started_on': now,
            'completed_on': now,
            'outcome': Build.SUCCEEDED
        }

    def _get_build_time_element(self, build):
        """
        Return the HTML element containing the build time for a build
        in the recent builds area
        """
        selector = 'div[data-latest-build-result="%s"] ' \
            '[data-role="data-recent-build-buildtime-field"]' % build.id

        # because this loads via Ajax, wait for it to be visible
        self.wait_until_present(selector)

        build_time_spans = self.find_all(selector)

        self.assertEqual(len(build_time_spans), 1)

        return build_time_spans[0]

    def _get_row_for_build(self, build):
        """ Get the table row for the build from the all builds table """
        self.wait_until_present('#allbuildstable')

        rows = self.find_all('#allbuildstable tr')

        # look for the row with a download link on the recipe which matches the
        # build ID
        url = reverse('builddashboard', args=(build.id,))
        selector = 'td.target a[href="%s"]' % url

        found_row = None
        for row in rows:

            outcome_links = row.find_elements_by_css_selector(selector)
            if len(outcome_links) == 1:
                found_row = row
                break

        self.assertNotEqual(found_row, None)

        return found_row

    def test_show_tasks_with_suffix(self):
        """ Task should be shown as suffix on build name """
        build = Build.objects.create(**self.project1_build_success)
        target = 'bash'
        task = 'clean'
        Target.objects.create(build=build, target=target, task=task)

        url = reverse('all-builds')
        self.get(url)
        self.wait_until_present('td[class="target"]')

        cell = self.find('td[class="target"]')
        content = cell.get_attribute('innerHTML')
        expected_text = '%s:%s' % (target, task)

        self.assertTrue(re.search(expected_text, content),
                        '"target" cell should contain text %s' % expected_text)

    def test_rebuild_buttons(self):
        """
        Test 'Rebuild' buttons in recent builds section

        'Rebuild' button should not be shown for command-line builds,
        but should be shown for other builds
        """
        build1 = Build.objects.create(**self.project1_build_success)
        default_build = Build.objects.create(**self.default_project_build_success)

        url = reverse('all-builds')
        self.get(url)

        # shouldn't see a rebuild button for command-line builds
        selector = 'div[data-latest-build-result="%s"] .rebuild-btn' % default_build.id
        run_again_button = self.find_all(selector)
        self.assertEqual(len(run_again_button), 0,
                         'should not see a rebuild button for cli builds')

        # should see a rebuild button for non-command-line builds
        selector = 'div[data-latest-build-result="%s"] .rebuild-btn' % build1.id
        run_again_button = self.find_all(selector)
        self.assertEqual(len(run_again_button), 1,
                         'should see a rebuild button for non-cli builds')

    def test_tooltips_on_project_name(self):
        """
        Test tooltips shown next to project name in the main table

        A tooltip should be present next to the command line
        builds project name in the all builds page, but not for
        other projects
        """
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.default_project_build_success)

        url = reverse('all-builds')
        self.get(url)

        # get the project name cells from the table
        cells = self.find_all('#allbuildstable td[class="project"]')

        selector = 'span.get-help'

        for cell in cells:
            content = cell.get_attribute('innerHTML')
            help_icons = cell.find_elements_by_css_selector(selector)

            if re.search(self.PROJECT_NAME, content):
                # no help icon next to non-cli project name
                msg = 'should not be a help icon for non-cli builds name'
                self.assertEqual(len(help_icons), 0, msg)
            elif re.search(self.CLI_BUILDS_PROJECT_NAME, content):
                # help icon next to cli project name
                msg = 'should be a help icon for cli builds name'
                self.assertEqual(len(help_icons), 1, msg)
            else:
                msg = 'found unexpected project name cell in all builds table'
                self.fail(msg)

    def test_builds_time_links(self):
        """
        Successful builds should have links on the time column and in the
        recent builds area; failed builds should not have links on the time column,
        or in the recent builds area
        """
        build1 = Build.objects.create(**self.project1_build_success)
        build2 = Build.objects.create(**self.project1_build_failure)

        # add some targets to these builds so they have recipe links
        # (and so we can find the row in the ToasterTable corresponding to
        # a particular build)
        Target.objects.create(build=build1, target='foo')
        Target.objects.create(build=build2, target='bar')

        url = reverse('all-builds')
        self.get(url)

        # test recent builds area for successful build
        element = self._get_build_time_element(build1)
        links = element.find_elements_by_css_selector('a')
        msg = 'should be a link on the build time for a successful recent build'
        self.assertEquals(len(links), 1, msg)

        # test recent builds area for failed build
        element = self._get_build_time_element(build2)
        links = element.find_elements_by_css_selector('a')
        msg = 'should not be a link on the build time for a failed recent build'
        self.assertEquals(len(links), 0, msg)

        # test the time column for successful build
        build1_row = self._get_row_for_build(build1)
        links = build1_row.find_elements_by_css_selector('td.time a')
        msg = 'should be a link on the build time for a successful build'
        self.assertEquals(len(links), 1, msg)

        # test the time column for failed build
        build2_row = self._get_row_for_build(build2)
        links = build2_row.find_elements_by_css_selector('td.time a')
        msg = 'should not be a link on the build time for a failed build'
        self.assertEquals(len(links), 0, msg)
