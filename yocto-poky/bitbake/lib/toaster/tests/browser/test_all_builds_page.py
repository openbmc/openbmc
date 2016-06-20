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

        self.default_project_build_success = {
            'project': self.default_project,
            'started_on': now,
            'completed_on': now,
            'outcome': Build.SUCCEEDED
        }

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

        # shouldn't see a run again button for command-line builds
        selector = 'div[data-latest-build-result="%s"] button' % default_build.id
        run_again_button = self.find_all(selector)
        self.assertEqual(len(run_again_button), 0,
                         'should not see a run again button for cli builds')

        # should see a run again button for non-command-line builds
        selector = 'div[data-latest-build-result="%s"] button' % build1.id
        run_again_button = self.find_all(selector)
        self.assertEqual(len(run_again_button), 1,
                         'should see a run again button for non-cli builds')

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

        selector = 'i.get-help'

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
