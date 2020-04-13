#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re

from django.urls import reverse
from django.utils import timezone
from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import BitbakeVersion, Release, Project, Build
from orm.models import ProjectVariable

class TestAllProjectsPage(SeleniumTestCase):
    """ Browser tests for projects page /projects/ """

    PROJECT_NAME = 'test project'
    CLI_BUILDS_PROJECT_NAME = 'command line builds'
    MACHINE_NAME = 'delorean'

    def setUp(self):
        """ Add default project manually """
        project = Project.objects.create_project(self.CLI_BUILDS_PROJECT_NAME, None)
        self.default_project = project
        self.default_project.is_default = True
        self.default_project.save()

        # this project is only set for some of the tests
        self.project = None

        self.release = None

    def _add_build_to_default_project(self):
        """ Add a build to the default project (not used in all tests) """
        now = timezone.now()
        build = Build.objects.create(project=self.default_project,
                                     started_on=now,
                                     completed_on=now)
        build.save()

    def _add_non_default_project(self):
        """ Add another project """
        bbv = BitbakeVersion.objects.create(name='test bbv', giturl='/tmp/',
                                            branch='master', dirpath='')
        self.release = Release.objects.create(name='test release',
                                              branch_name='master',
                                              bitbake_version=bbv)
        self.project = Project.objects.create_project(self.PROJECT_NAME, self.release)
        self.project.is_default = False
        self.project.save()

        # fake the MACHINE variable
        project_var = ProjectVariable.objects.create(project=self.project,
                                                     name='MACHINE',
                                                     value=self.MACHINE_NAME)
        project_var.save()

    def _get_row_for_project(self, project_name):
        """ Get the HTML row for a project, or None if not found """
        self.wait_until_present('#projectstable tbody tr')
        rows = self.find_all('#projectstable tbody tr')

        # find the row with a project name matching the one supplied
        found_row = None
        for row in rows:
            if re.search(project_name, row.get_attribute('innerHTML')):
                found_row = row
                break

        return found_row

    def test_default_project_hidden(self):
        """
        The default project should be hidden if it has no builds
        and we should see the "no results" area
        """
        url = reverse('all-projects')
        self.get(url)
        self.wait_until_visible('#empty-state-projectstable')

        rows = self.find_all('#projectstable tbody tr')
        self.assertEqual(len(rows), 0, 'should be no projects displayed')

    def test_default_project_has_build(self):
        """ The default project should be shown if it has builds """
        self._add_build_to_default_project()

        url = reverse('all-projects')
        self.get(url)

        default_project_row = self._get_row_for_project(self.default_project.name)

        self.assertNotEqual(default_project_row, None,
                            'default project "cli builds" should be in page')

    def test_default_project_release(self):
        """
        The release for the default project should display as
        'Not applicable'
        """
        # need a build, otherwise project doesn't display at all
        self._add_build_to_default_project()

        # another project to test, which should show release
        self._add_non_default_project()

        self.get(reverse('all-projects'))
        self.wait_until_visible("#projectstable tr")

        # find the row for the default project
        default_project_row = self._get_row_for_project(self.default_project.name)

        # check the release text for the default project
        selector = 'span[data-project-field="release"] span.text-muted'
        element = default_project_row.find_element_by_css_selector(selector)
        text = element.text.strip()
        self.assertEqual(text, 'Not applicable',
                         'release should be "not applicable" for default project')

        # find the row for the default project
        other_project_row = self._get_row_for_project(self.project.name)

        # check the link in the release cell for the other project
        selector = 'span[data-project-field="release"]'
        element = other_project_row.find_element_by_css_selector(selector)
        text = element.text.strip()
        self.assertEqual(text, self.release.name,
                         'release name should be shown for non-default project')

    def test_default_project_machine(self):
        """
        The machine for the default project should display as
        'Not applicable'
        """
        # need a build, otherwise project doesn't display at all
        self._add_build_to_default_project()

        # another project to test, which should show machine
        self._add_non_default_project()

        self.get(reverse('all-projects'))

        self.wait_until_visible("#projectstable tr")

        # find the row for the default project
        default_project_row = self._get_row_for_project(self.default_project.name)

        # check the machine cell for the default project
        selector = 'span[data-project-field="machine"] span.text-muted'
        element = default_project_row.find_element_by_css_selector(selector)
        text = element.text.strip()
        self.assertEqual(text, 'Not applicable',
                         'machine should be not applicable for default project')

        # find the row for the default project
        other_project_row = self._get_row_for_project(self.project.name)

        # check the link in the machine cell for the other project
        selector = 'span[data-project-field="machine"]'
        element = other_project_row.find_element_by_css_selector(selector)
        text = element.text.strip()
        self.assertEqual(text, self.MACHINE_NAME,
                         'machine name should be shown for non-default project')

    def test_project_page_links(self):
        """
        Test that links for the default project point to the builds
        page /projects/X/builds for that project, and that links for
        other projects point to their configuration pages /projects/X/
        """

        # need a build, otherwise project doesn't display at all
        self._add_build_to_default_project()

        # another project to test
        self._add_non_default_project()

        self.get(reverse('all-projects'))

        # find the row for the default project
        default_project_row = self._get_row_for_project(self.default_project.name)

        # check the link on the name field
        selector = 'span[data-project-field="name"] a'
        element = default_project_row.find_element_by_css_selector(selector)
        link_url = element.get_attribute('href').strip()
        expected_url = reverse('projectbuilds', args=(self.default_project.id,))
        msg = 'link on default project name should point to builds but was %s' % link_url
        self.assertTrue(link_url.endswith(expected_url), msg)

        # find the row for the other project
        other_project_row = self._get_row_for_project(self.project.name)

        # check the link for the other project
        selector = 'span[data-project-field="name"] a'
        element = other_project_row.find_element_by_css_selector(selector)
        link_url = element.get_attribute('href').strip()
        expected_url = reverse('project', args=(self.project.id,))
        msg = 'link on project name should point to configuration but was %s' % link_url
        self.assertTrue(link_url.endswith(expected_url), msg)
