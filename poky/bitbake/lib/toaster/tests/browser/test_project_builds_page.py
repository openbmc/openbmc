#! /usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
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

import re

from django.core.urlresolvers import reverse
from django.utils import timezone
from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import BitbakeVersion, Release, Project, Build, Target

class TestProjectBuildsPage(SeleniumTestCase):
    """ Test data at /project/X/builds is displayed correctly """

    PROJECT_NAME = 'test project'
    CLI_BUILDS_PROJECT_NAME = 'command line builds'

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name='bbv1', giturl='/tmp/',
                                            branch='master', dirpath='')
        release = Release.objects.create(name='release1',
                                         bitbake_version=bbv)
        self.project1 = Project.objects.create_project(name=self.PROJECT_NAME,
                                                       release=release)
        self.project1.save()

        self.project2 = Project.objects.create_project(name=self.PROJECT_NAME,
                                                       release=release)
        self.project2.save()

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

        self.project1_build_in_progress = {
            'project': self.project1,
            'started_on': now,
            'completed_on': now,
            'outcome': Build.IN_PROGRESS
        }

        self.project2_build_success = {
            'project': self.project2,
            'started_on': now,
            'completed_on': now,
            'outcome': Build.SUCCEEDED
        }

        self.project2_build_in_progress = {
            'project': self.project2,
            'started_on': now,
            'completed_on': now,
            'outcome': Build.IN_PROGRESS
        }

    def _get_rows_for_project(self, project_id):
        """
        Helper to retrieve HTML rows for a project's builds,
        as shown in the main table of the page
        """
        url = reverse('projectbuilds', args=(project_id,))
        self.get(url)
        self.wait_until_present('#projectbuildstable tbody tr')
        return self.find_all('#projectbuildstable tbody tr')

    def test_show_builds_for_project(self):
        """ Builds for a project should be displayed in the main table """
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.project1_build_success)
        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 2)

    def test_show_builds_project_only(self):
        """ Builds for other projects should be excluded """
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.project1_build_success)

        # shouldn't see these two
        Build.objects.create(**self.project2_build_success)
        Build.objects.create(**self.project2_build_in_progress)

        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 3)

    def test_builds_exclude_in_progress(self):
        """ "in progress" builds should not be shown in main table """
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.project1_build_success)

        # shouldn't see this one
        Build.objects.create(**self.project1_build_in_progress)

        # shouldn't see these two either, as they belong to a different project
        Build.objects.create(**self.project2_build_success)
        Build.objects.create(**self.project2_build_in_progress)

        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 2)

    def test_show_tasks_with_suffix(self):
        """ Task should be shown as suffixes on build names """
        build = Build.objects.create(**self.project1_build_success)
        target = 'bash'
        task = 'clean'
        Target.objects.create(build=build, target=target, task=task)

        url = reverse('projectbuilds', args=(self.project1.id,))
        self.get(url)
        self.wait_until_present('td[class="target"]')

        cell = self.find('td[class="target"]')
        content = cell.get_attribute('innerHTML')
        expected_text = '%s:%s' % (target, task)

        self.assertTrue(re.search(expected_text, content),
                        '"target" cell should contain text %s' % expected_text)

    def test_cli_builds_hides_tabs(self):
        """
        Display for command line builds should hide tabs
        """
        url = reverse('projectbuilds', args=(self.default_project.id,))
        self.get(url)
        tabs = self.find_all('#project-topbar')
        self.assertEqual(len(tabs), 0,
                         'should be no top bar shown for command line builds')

    def test_non_cli_builds_has_tabs(self):
        """
        Non-command-line builds projects should show the tabs
        """
        url = reverse('projectbuilds', args=(self.project1.id,))
        self.get(url)
        tabs = self.find_all('#project-topbar')
        self.assertEqual(len(tabs), 1,
                         'should be a top bar shown for non-command-line builds')
