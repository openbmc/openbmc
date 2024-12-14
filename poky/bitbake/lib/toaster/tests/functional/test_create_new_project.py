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
from selenium.webdriver.common.by import By

class TestCreateNewProject(SeleniumFunctionalTestCase):

    def test_create_new_project_master(self):
        """ Test create new project using:
          - Project Name: Any string
          - Release: Yocto Project master (option value: 3)
          - Merge Toaster settings: False
        """
        release = '3'
        release_title = 'Yocto Project master'
        project_name = 'projectmaster'
        self.create_new_project(
            project_name,
            release,
            release_title,
            False,
        )

    def test_create_new_project_scarthgap(self):
        """ Test create new project using:
          - Project Name: Any string
          - Release: Yocto Project 5.0 "Scarthgap" (option value: 1)
          - Merge Toaster settings: True
        """
        release = '1'
        release_title = 'Yocto Project 5.0 "Scarthgap"'
        project_name = 'projectscarthgap'
        self.create_new_project(
            project_name,
            release,
            release_title,
            True,
        )

    def test_create_new_project_kirkstone(self):
        """ Test create new project using:
          - Project Name: Any string
          - Release: Yocto Project 4.0 "Kirkstone" (option value: 4)
          - Merge Toaster settings: True
        """
        release = '5'
        release_title = 'Yocto Project 4.0 "Kirkstone"'
        project_name = 'projectkirkstone'
        self.create_new_project(
            project_name,
            release,
            release_title,
            True,
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
        self.create_new_project(
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

        self.wait_until_visible('.alert-danger')

        # check error message
        self.assertTrue(self.element_exists('.alert-danger'),
                        'Alert message not shown')
        self.assertTrue(wrong_path in self.find('.alert-danger').text,
                        "Wrong path not in alert message")
