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
from selenium.webdriver.support.select import Select
from django.utils import timezone
from bldcontrol.models import BuildRequest
from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import BitbakeVersion, Layer, Layer_Version, Recipe, Release, Project, Build, Target, Task

from selenium.webdriver.common.by import By


class TestAllBuildsPage(SeleniumTestCase):
    """ Tests for all builds page /builds/ """

    PROJECT_NAME = 'test project'
    CLI_BUILDS_PROJECT_NAME = 'command line builds'

    def setUp(self):
        builldir = os.environ.get('BUILDDIR', './')
        bbv = BitbakeVersion.objects.create(name='bbv1', giturl=f'{builldir}/',
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
        self.wait_until_visible(selector)

        build_time_spans = self.find_all(selector)

        self.assertEqual(len(build_time_spans), 1)

        return build_time_spans[0]

    def _get_row_for_build(self, build):
        """ Get the table row for the build from the all builds table """
        self.wait_until_visible('#allbuildstable')

        rows = self.find_all('#allbuildstable tr')

        # look for the row with a download link on the recipe which matches the
        # build ID
        url = reverse('builddashboard', args=(build.id,))
        selector = 'td.target a[href="%s"]' % url

        found_row = None
        for row in rows:

            outcome_links = row.find_elements(By.CSS_SELECTOR, selector)
            if len(outcome_links) == 1:
                found_row = row
                break

        self.assertNotEqual(found_row, None)

        return found_row

    def _get_create_builds(self, **kwargs):
        """ Create a build and return the build object """
        build1 = Build.objects.create(**self.project1_build_success)
        build2 = Build.objects.create(**self.project1_build_failure)

        # add some targets to these builds so they have recipe links
        # (and so we can find the row in the ToasterTable corresponding to
        # a particular build)
        Target.objects.create(build=build1, target='foo')
        Target.objects.create(build=build2, target='bar')

        if kwargs:
            # Create kwargs.get('success') builds with success status with target
            # and kwargs.get('failure') builds with failure status with target
            for i in range(kwargs.get('success', 0)):
                now = timezone.now()
                self.project1_build_success['started_on'] = now
                self.project1_build_success[
                    'completed_on'] = now - timezone.timedelta(days=i)
                build = Build.objects.create(**self.project1_build_success)
                Target.objects.create(build=build,
                                      target=f'{i}_success_recipe',
                                      task=f'{i}_success_task')

                self._set_buildRequest_and_task_on_build(build)
            for i in range(kwargs.get('failure', 0)):
                now = timezone.now()
                self.project1_build_failure['started_on'] = now
                self.project1_build_failure[
                    'completed_on'] = now - timezone.timedelta(days=i)
                build = Build.objects.create(**self.project1_build_failure)
                Target.objects.create(build=build,
                                      target=f'{i}_fail_recipe',
                                      task=f'{i}_fail_task')
                self._set_buildRequest_and_task_on_build(build)
        return build1, build2

    def _create_recipe(self):
        """ Add a recipe to the database and return it """
        layer = Layer.objects.create()
        layer_version = Layer_Version.objects.create(layer=layer)
        return Recipe.objects.create(name='recipe_foo', layer_version=layer_version)

    def _set_buildRequest_and_task_on_build(self, build):
        """ Set buildRequest and task on build """
        build.recipes_parsed = 1
        build.save()
        buildRequest = BuildRequest.objects.create(
            build=build,
            project=self.project1,
            state=BuildRequest.REQ_COMPLETED)
        build.build_request = buildRequest
        recipe = self._create_recipe()
        task = Task.objects.create(build=build,
                                   recipe=recipe,
                                   task_name='task',
                                   outcome=Task.OUTCOME_SUCCESS)
        task.save()
        build.save()

    def test_show_tasks_with_suffix(self):
        """ Task should be shown as suffix on build name """
        build = Build.objects.create(**self.project1_build_success)
        target = 'bash'
        task = 'clean'
        Target.objects.create(build=build, target=target, task=task)

        url = reverse('all-builds')
        self.get(url)
        self.wait_until_visible('td[class="target"]')

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
        default_build = Build.objects.create(
            **self.default_project_build_success)

        url = reverse('all-builds')
        self.get(url)

        # should see a rebuild button for non-command-line builds
        self.wait_until_visible('#allbuildstable tbody tr')
        self.wait_until_visible('.rebuild-btn')
        selector = 'div[data-latest-build-result="%s"] .rebuild-btn' % build1.id
        run_again_button = self.find_all(selector)
        self.assertEqual(len(run_again_button), 1,
                         'should see a rebuild button for non-cli builds')

        # shouldn't see a rebuild button for command-line builds
        selector = 'div[data-latest-build-result="%s"] .rebuild-btn' % default_build.id
        run_again_button = self.find_all(selector)
        self.assertEqual(len(run_again_button), 0,
                         'should not see a rebuild button for cli builds')

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
        self.wait_until_visible('#allbuildstable')

        # get the project name cells from the table
        cells = self.find_all('#allbuildstable td[class="project"]')

        selector = 'span.get-help'

        for cell in cells:
            content = cell.get_attribute('innerHTML')
            help_icons = cell.find_elements(By.CSS_SELECTOR, selector)

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
        build1, build2 = self._get_create_builds()

        url = reverse('all-builds')
        self.get(url)
        self.wait_until_visible('#allbuildstable')

        # test recent builds area for successful build
        element = self._get_build_time_element(build1)
        links = element.find_elements(By.CSS_SELECTOR, 'a')
        msg = 'should be a link on the build time for a successful recent build'
        self.assertEqual(len(links), 1, msg)

        # test recent builds area for failed build
        element = self._get_build_time_element(build2)
        links = element.find_elements(By.CSS_SELECTOR, 'a')
        msg = 'should not be a link on the build time for a failed recent build'
        self.assertEqual(len(links), 0, msg)

        # test the time column for successful build
        build1_row = self._get_row_for_build(build1)
        links = build1_row.find_elements(By.CSS_SELECTOR, 'td.time a')
        msg = 'should be a link on the build time for a successful build'
        self.assertEqual(len(links), 1, msg)

        # test the time column for failed build
        build2_row = self._get_row_for_build(build2)
        links = build2_row.find_elements(By.CSS_SELECTOR, 'td.time a')
        msg = 'should not be a link on the build time for a failed build'
        self.assertEqual(len(links), 0, msg)

    def test_builds_table_search_box(self):
        """ Test the search box in the builds table on the all builds page """
        self._get_create_builds()

        url = reverse('all-builds')
        self.get(url)

        # Check search box is present and works
        self.wait_until_visible('#allbuildstable tbody tr')
        search_box = self.find('#search-input-allbuildstable')
        self.assertTrue(search_box.is_displayed())

        # Check that we can search for a build by recipe name
        search_box.send_keys('foo')
        search_btn = self.find('#search-submit-allbuildstable')
        search_btn.click()
        self.wait_until_visible('#allbuildstable tbody tr')
        rows = self.find_all('#allbuildstable tbody tr')
        self.assertTrue(len(rows) >= 1)

    def test_filtering_on_failure_tasks_column(self):
        """ Test the filtering on failure tasks column in the builds table on the all builds page """
        def _check_if_filter_failed_tasks_column_is_visible():
            # check if failed tasks filter column is visible, if not click on it
            # Check edit column
            edit_column = self.find('#edit-columns-button')
            self.assertTrue(edit_column.is_displayed())
            edit_column.click()
            # Check dropdown is visible
            self.wait_until_visible('ul.dropdown-menu.editcol')
            filter_fails_task_checkbox = self.find('#checkbox-failed_tasks')
            if not filter_fails_task_checkbox.is_selected():
                filter_fails_task_checkbox.click()
            edit_column.click()

        self._get_create_builds(success=10, failure=10)

        url = reverse('all-builds')
        self.get(url)

        # Check filtering on failure tasks column
        self.wait_until_visible('#allbuildstable tbody tr')
        _check_if_filter_failed_tasks_column_is_visible()
        failed_tasks_filter = self.find('#failed_tasks_filter')
        failed_tasks_filter.click()
        # Check popup is visible
        self.wait_until_visible('#filter-modal-allbuildstable')
        self.assertTrue(
            self.find('#filter-modal-allbuildstable').is_displayed())
        # Check that we can filter by failure tasks
        build_without_failure_tasks = self.find(
            '#failed_tasks_filter\\:without_failed_tasks')
        build_without_failure_tasks.click()
        # click on apply button
        self.find('#filter-modal-allbuildstable .btn-primary').click()
        self.wait_until_visible('#allbuildstable tbody tr')
        # Check if filter is applied, by checking if failed_tasks_filter has btn-primary class
        self.assertTrue(self.find('#failed_tasks_filter').get_attribute(
            'class').find('btn-primary') != -1)

    def test_filtering_on_completedOn_column(self):
        """ Test the filtering on completed_on column in the builds table on the all builds page """
        self._get_create_builds(success=10, failure=10)

        url = reverse('all-builds')
        self.get(url)

        # Check filtering on failure tasks column
        self.wait_until_visible('#allbuildstable tbody tr')
        completed_on_filter = self.find('#completed_on_filter')
        completed_on_filter.click()
        # Check popup is visible
        self.wait_until_visible('#filter-modal-allbuildstable')
        self.assertTrue(
            self.find('#filter-modal-allbuildstable').is_displayed())
        # Check that we can filter by failure tasks
        build_without_failure_tasks = self.find(
            '#completed_on_filter\\:date_range')
        build_without_failure_tasks.click()
        # click on apply button
        self.find('#filter-modal-allbuildstable .btn-primary').click()
        self.wait_until_visible('#allbuildstable tbody tr')
        # Check if filter is applied, by checking if completed_on_filter has btn-primary class
        self.assertTrue(self.find('#completed_on_filter').get_attribute(
            'class').find('btn-primary') != -1)

        # Filter by date range
        self.find('#completed_on_filter').click()
        self.wait_until_visible('#filter-modal-allbuildstable')
        date_ranges = self.driver.find_elements(
            By.XPATH, '//input[@class="form-control hasDatepicker"]')
        today = timezone.now()
        yestersday = today - timezone.timedelta(days=1)
        date_ranges[0].send_keys(yestersday.strftime('%Y-%m-%d'))
        date_ranges[1].send_keys(today.strftime('%Y-%m-%d'))
        self.find('#filter-modal-allbuildstable .btn-primary').click()
        self.wait_until_visible('#allbuildstable tbody tr')
        self.assertTrue(self.find('#completed_on_filter').get_attribute(
            'class').find('btn-primary') != -1)
        # Check if filter is applied, number of builds displayed should be 6
        self.assertTrue(len(self.find_all('#allbuildstable tbody tr')) >= 4)

    def test_builds_table_editColumn(self):
        """ Test the edit column feature in the builds table on the all builds page """
        self._get_create_builds(success=10, failure=10)

        def test_edit_column(check_box_id):
            # Check that we can hide/show table column
            check_box = self.find(f'#{check_box_id}')
            th_class = str(check_box_id).replace('checkbox-', '')
            if check_box.is_selected():
                # check if column is visible in table
                self.assertTrue(
                    self.find(
                        f'#allbuildstable thead th.{th_class}'
                    ).is_displayed(),
                    f"The {th_class} column is checked in EditColumn dropdown, but it's not visible in table"
                )
                check_box.click()
                # check if column is hidden in table
                self.assertFalse(
                    self.find(
                        f'#allbuildstable thead th.{th_class}'
                    ).is_displayed(),
                    f"The {th_class} column is unchecked in EditColumn dropdown, but it's visible in table"
                )
            else:
                # check if column is hidden in table
                self.assertFalse(
                    self.find(
                        f'#allbuildstable thead th.{th_class}'
                    ).is_displayed(),
                    f"The {th_class} column is unchecked in EditColumn dropdown, but it's visible in table"
                )
                check_box.click()
                # check if column is visible in table
                self.assertTrue(
                    self.find(
                        f'#allbuildstable thead th.{th_class}'
                    ).is_displayed(),
                    f"The {th_class} column is checked in EditColumn dropdown, but it's not visible in table"
                )
        url = reverse('all-builds')
        self.get(url)
        self.wait_until_visible('#allbuildstable tbody tr')

        # Check edit column
        edit_column = self.find('#edit-columns-button')
        self.assertTrue(edit_column.is_displayed())
        edit_column.click()
        # Check dropdown is visible
        self.wait_until_visible('ul.dropdown-menu.editcol')

        # Check that we can hide the edit column
        test_edit_column('checkbox-errors_no')
        test_edit_column('checkbox-failed_tasks')
        test_edit_column('checkbox-image_files')
        test_edit_column('checkbox-project')
        test_edit_column('checkbox-started_on')
        test_edit_column('checkbox-time')
        test_edit_column('checkbox-warnings_no')

    def test_builds_table_show_rows(self):
        """ Test the show rows feature in the builds table on the all builds page """
        self._get_create_builds(success=100, failure=100)

        def test_show_rows(row_to_show, show_row_link):
            # Check that we can show rows == row_to_show
            show_row_link.select_by_value(str(row_to_show))
            self.wait_until_visible('#allbuildstable tbody tr')
            # check at least some rows are visible
            self.assertTrue(
                len(self.find_all('#allbuildstable tbody tr')) > 0
            )

        url = reverse('all-builds')
        self.get(url)
        self.wait_until_visible('#allbuildstable tbody tr')

        show_rows = self.driver.find_elements(
            By.XPATH,
            '//select[@class="form-control pagesize-allbuildstable"]'
        )
        # Check show rows
        for show_row_link in show_rows:
            show_row_link = Select(show_row_link)
            test_show_rows(10, show_row_link)
            test_show_rows(25, show_row_link)
            test_show_rows(50, show_row_link)
            test_show_rows(100, show_row_link)
            test_show_rows(150, show_row_link)
