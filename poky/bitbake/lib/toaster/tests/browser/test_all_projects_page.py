#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import re

from django.urls import reverse
from django.utils import timezone
from selenium.webdriver.support.select import Select
from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import BitbakeVersion, Release, Project, Build
from orm.models import ProjectVariable

from selenium.webdriver.common.by import By


class TestAllProjectsPage(SeleniumTestCase):
    """ Browser tests for projects page /projects/ """

    PROJECT_NAME = 'test project'
    CLI_BUILDS_PROJECT_NAME = 'command line builds'
    MACHINE_NAME = 'delorean'

    def setUp(self):
        """ Add default project manually """
        project = Project.objects.create_project(
            self.CLI_BUILDS_PROJECT_NAME, None)
        self.default_project = project
        self.default_project.is_default = True
        self.default_project.save()

        # this project is only set for some of the tests
        self.project = None

        self.release = None

    def _create_projects(self, nb_project=10):
        projects = []
        for i in range(1, nb_project + 1):
            projects.append(
                Project(
                    name='test project {}'.format(i),
                    release=self.release,
                )
            )
        Project.objects.bulk_create(projects)

    def _add_build_to_default_project(self):
        """ Add a build to the default project (not used in all tests) """
        now = timezone.now()
        build = Build.objects.create(project=self.default_project,
                                     started_on=now,
                                     completed_on=now)
        build.save()

    def _add_non_default_project(self):
        """ Add another project """
        builldir = os.environ.get('BUILDDIR', './')
        bbv = BitbakeVersion.objects.create(name='test bbv', giturl=f'{builldir}/',
                                            branch='master', dirpath='')
        self.release = Release.objects.create(name='test release',
                                              branch_name='master',
                                              bitbake_version=bbv)
        self.project = Project.objects.create_project(
            self.PROJECT_NAME, self.release)
        self.project.is_default = False
        self.project.save()

        # fake the MACHINE variable
        project_var = ProjectVariable.objects.create(project=self.project,
                                                     name='MACHINE',
                                                     value=self.MACHINE_NAME)
        project_var.save()

    def _get_row_for_project(self, project_name):
        """ Get the HTML row for a project, or None if not found """
        self.wait_until_visible('#projectstable tbody tr')
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

        default_project_row = self._get_row_for_project(
            self.default_project.name)

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
        default_project_row = self._get_row_for_project(
            self.default_project.name)

        # check the release text for the default project
        selector = 'span[data-project-field="release"] span.text-muted'
        element = default_project_row.find_element(By.CSS_SELECTOR, selector)
        text = element.text.strip()
        self.assertEqual(text, 'Not applicable',
                         'release should be "not applicable" for default project')

        # find the row for the default project
        other_project_row = self._get_row_for_project(self.project.name)

        # check the link in the release cell for the other project
        selector = 'span[data-project-field="release"]'
        element = other_project_row.find_element(By.CSS_SELECTOR, selector)
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
        default_project_row = self._get_row_for_project(
            self.default_project.name)

        # check the machine cell for the default project
        selector = 'span[data-project-field="machine"] span.text-muted'
        element = default_project_row.find_element(By.CSS_SELECTOR, selector)
        text = element.text.strip()
        self.assertEqual(text, 'Not applicable',
                         'machine should be not applicable for default project')

        # find the row for the default project
        other_project_row = self._get_row_for_project(self.project.name)

        # check the link in the machine cell for the other project
        selector = 'span[data-project-field="machine"]'
        element = other_project_row.find_element(By.CSS_SELECTOR, selector)
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
        default_project_row = self._get_row_for_project(
            self.default_project.name)

        # check the link on the name field
        selector = 'span[data-project-field="name"] a'
        element = default_project_row.find_element(By.CSS_SELECTOR, selector)
        link_url = element.get_attribute('href').strip()
        expected_url = reverse(
            'projectbuilds', args=(self.default_project.id,))
        msg = 'link on default project name should point to builds but was %s' % link_url
        self.assertTrue(link_url.endswith(expected_url), msg)

        # find the row for the other project
        other_project_row = self._get_row_for_project(self.project.name)

        # check the link for the other project
        selector = 'span[data-project-field="name"] a'
        element = other_project_row.find_element(By.CSS_SELECTOR, selector)
        link_url = element.get_attribute('href').strip()
        expected_url = reverse('project', args=(self.project.id,))
        msg = 'link on project name should point to configuration but was %s' % link_url
        self.assertTrue(link_url.endswith(expected_url), msg)

    def test_allProject_table_search_box(self):
        """ Test the search box in the all project table on the all projects page """
        self._create_projects()

        url = reverse('all-projects')
        self.get(url)

        # Chseck search box is present and works
        self.wait_until_visible('#projectstable tbody tr')
        search_box = self.find('#search-input-projectstable')
        self.assertTrue(search_box.is_displayed())

        # Check that we can search for a project by project name
        search_box.send_keys('test project 10')
        search_btn = self.find('#search-submit-projectstable')
        search_btn.click()
        self.wait_until_visible('#projectstable tbody tr')
        rows = self.find_all('#projectstable tbody tr')
        self.assertTrue(len(rows) == 1)

    def test_allProject_table_editColumn(self):
        """ Test the edit column feature in the projects table on the all projects page """
        self._create_projects()

        def test_edit_column(check_box_id):
            # Check that we can hide/show table column
            check_box = self.find(f'#{check_box_id}')
            th_class = str(check_box_id).replace('checkbox-', '')
            if check_box.is_selected():
                # check if column is visible in table
                self.assertTrue(
                    self.find(
                        f'#projectstable thead th.{th_class}'
                    ).is_displayed(),
                    f"The {th_class} column is checked in EditColumn dropdown, but it's not visible in table"
                )
                check_box.click()
                # check if column is hidden in table
                self.assertFalse(
                    self.find(
                        f'#projectstable thead th.{th_class}'
                    ).is_displayed(),
                    f"The {th_class} column is unchecked in EditColumn dropdown, but it's visible in table"
                )
            else:
                # check if column is hidden in table
                self.assertFalse(
                    self.find(
                        f'#projectstable thead th.{th_class}'
                    ).is_displayed(),
                    f"The {th_class} column is unchecked in EditColumn dropdown, but it's visible in table"
                )
                check_box.click()
                # check if column is visible in table
                self.assertTrue(
                    self.find(
                        f'#projectstable thead th.{th_class}'
                    ).is_displayed(),
                    f"The {th_class} column is checked in EditColumn dropdown, but it's not visible in table"
                )
        url = reverse('all-projects')
        self.get(url)
        self.wait_until_visible('#projectstable tbody tr')

        # Check edit column
        edit_column = self.find('#edit-columns-button')
        self.assertTrue(edit_column.is_displayed())
        edit_column.click()
        # Check dropdown is visible
        self.wait_until_visible('ul.dropdown-menu.editcol')

        # Check that we can hide the edit column
        test_edit_column('checkbox-errors')
        test_edit_column('checkbox-image_files')
        test_edit_column('checkbox-last_build_outcome')
        test_edit_column('checkbox-recipe_name')
        test_edit_column('checkbox-warnings')

    def test_allProject_table_show_rows(self):
        """ Test the show rows feature in the projects table on the all projects page """
        self._create_projects(nb_project=200)

        def test_show_rows(row_to_show, show_row_link):
            # Check that we can show rows == row_to_show
            show_row_link.select_by_value(str(row_to_show))
            self.wait_until_visible('#projectstable tbody tr')
            # check at least some rows are visible
            self.assertTrue(
                len(self.find_all('#projectstable tbody tr')) > 0
            )

        url = reverse('all-projects')
        self.get(url)
        self.wait_until_visible('#projectstable tbody tr')

        show_rows = self.driver.find_elements(
            By.XPATH,
            '//select[@class="form-control pagesize-projectstable"]'
        )
        # Check show rows
        for show_row_link in show_rows:
            show_row_link = Select(show_row_link)
            test_show_rows(10, show_row_link)
            test_show_rows(25, show_row_link)
            test_show_rows(50, show_row_link)
            test_show_rows(100, show_row_link)
            test_show_rows(150, show_row_link)
