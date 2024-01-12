#! /usr/bin/env python3
# BitBake Toaster UI tests implementation
#
# Copyright (C) 2023 Savoir-faire Linux
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re
import pytest
from django.urls import reverse
from selenium.webdriver.support.select import Select
from tests.functional.functional_helpers import SeleniumFunctionalTestCase
from orm.models import Project
from selenium.webdriver.common.by import By


@pytest.mark.django_db
@pytest.mark.order("last")
class TestCreateNewProject(SeleniumFunctionalTestCase):

    def _create_test_new_project(
        self,
        project_name,
        release,
        release_title,
        merge_toaster_settings,
    ):
        """ Create/Test new project using:
          - Project Name: Any string
          - Release: Any string
          - Merge Toaster settings: True or False
        """
        self.get(reverse('newproject'))
        self.wait_until_visible('#new-project-name', poll=3)
        self.driver.find_element(By.ID,
                                 "new-project-name").send_keys(project_name)

        select = Select(self.find('#projectversion'))
        select.select_by_value(release)

        # check merge toaster settings
        checkbox = self.find('.checkbox-mergeattr')
        if merge_toaster_settings:
            if not checkbox.is_selected():
                checkbox.click()
        else:
            if checkbox.is_selected():
                checkbox.click()

        self.driver.find_element(By.ID, "create-project-button").click()

        element = self.wait_until_visible('#project-created-notification', poll=3)
        self.assertTrue(
            self.element_exists('#project-created-notification'),
            f"Project:{project_name} creation notification not shown"
        )
        self.assertTrue(
            project_name in element.text,
            f"New project name:{project_name} not in new project notification"
        )
        self.assertTrue(
            Project.objects.filter(name=project_name).count(),
            f"New project:{project_name} not found in database"
        )

        # check release
        self.assertTrue(re.search(
            release_title,
            self.driver.find_element(By.XPATH,
                                     "//span[@id='project-release-title']"
                                     ).text),
                        'The project release is not defined')

    def test_create_new_project_master(self):
        """ Test create new project using:
          - Project Name: Any string
          - Release: Yocto Project master (option value: 3)
          - Merge Toaster settings: False
        """
        release = '3'
        release_title = 'Yocto Project master'
        project_name = 'projectmaster'
        self._create_test_new_project(
            project_name,
            release,
            release_title,
            False,
        )

    def test_create_new_project_kirkstone(self):
        """ Test create new project using:
          - Project Name: Any string
          - Release: Yocto Project 4.0 "Kirkstone" (option value: 1)
          - Merge Toaster settings: True
        """
        release = '1'
        release_title = 'Yocto Project 4.0 "Kirkstone"'
        project_name = 'projectkirkstone'
        self._create_test_new_project(
            project_name,
            release,
            release_title,
            True,
        )

    def test_create_new_project_dunfell(self):
        """ Test create new project using:
          - Project Name: Any string
          - Release: Yocto Project 3.1 "Dunfell" (option value: 5)
          - Merge Toaster settings: False
        """
        release = '5'
        release_title = 'Yocto Project 3.1 "Dunfell"'
        project_name = 'projectdunfell'
        self._create_test_new_project(
            project_name,
            release,
            release_title,
            False,
        )

    def test_create_new_project_local(self):
        """ Test create new project using:
          - Project Name: Any string
          - Release: Local Yocto Project (option value: 2)
          - Merge Toaster settings: True
        """
        release = '2'
        release_title = 'Local Yocto Project'
        project_name = 'projectlocal'
        self._create_test_new_project(
            project_name,
            release,
            release_title,
            True,
        )

    def test_create_new_project_without_name(self):
        """ Test create new project without project name """
        self.get(reverse('newproject'))

        select = Select(self.find('#projectversion'))
        select.select_by_value(str(3))

        # Check input name has required attribute
        input_name = self.driver.find_element(By.ID, "new-project-name")
        self.assertIsNotNone(input_name.get_attribute('required'),
                        'Input name has not required attribute')

        # Check create button is disabled
        create_btn = self.driver.find_element(By.ID, "create-project-button")
        self.assertIsNotNone(create_btn.get_attribute('disabled'),
                        'Create button is not disabled')

    def test_import_new_project(self):
        """ Test import new project using:
          - Project Name: Any string
          - Project type: select (Import command line project)
          - Import existing project directory: Wrong Path
        """
        project_name = 'projectimport'
        self.get(reverse('newproject'))
        self.driver.find_element(By.ID,
                                 "new-project-name").send_keys(project_name)
        # select import project
        self.find('#type-import').click()

        # set wrong path
        wrong_path = '/wrongpath'
        self.driver.find_element(By.ID,
                                 "import-project-dir").send_keys(wrong_path)
        self.driver.find_element(By.ID, "create-project-button").click()

        # check error message
        self.assertTrue(self.element_exists('.alert-danger'),
                        'Allert message not shown')
        self.assertTrue(wrong_path in self.find('.alert-danger').text,
                        "Wrong path not in alert message")
