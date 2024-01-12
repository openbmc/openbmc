#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
from django.urls import reverse
from tests.browser.selenium_helpers import SeleniumTestCase
from selenium.webdriver.support.ui import Select
from selenium.common.exceptions import InvalidElementStateException
from selenium.webdriver.common.by import By

from orm.models import Project, Release, BitbakeVersion


class TestNewProjectPage(SeleniumTestCase):
    """ Test project data at /project/X/ is displayed correctly """

    def setUp(self):
        bitbake, c = BitbakeVersion.objects.get_or_create(
            name="master",
            giturl="git://master",
            branch="master",
            dirpath="master")

        release, c = Release.objects.get_or_create(name="msater",
                                                   description="master"
                                                   "release",
                                                   branch_name="master",
                                                   helptext="latest",
                                                   bitbake_version=bitbake)

        self.release, c = Release.objects.get_or_create(
            name="msater2",
            description="master2"
            "release2",
            branch_name="master2",
            helptext="latest2",
            bitbake_version=bitbake)

    def test_create_new_project(self):
        """ Test creating a project """

        project_name = "masterproject"

        url = reverse('newproject')
        self.get(url)
        self.wait_until_visible('#new-project-name', poll=3)
        self.enter_text('#new-project-name', project_name)

        select = Select(self.find('#projectversion'))
        select.select_by_value(str(self.release.pk))

        self.click("#create-project-button")

        # We should get redirected to the new project's page with the
        # notification at the top
        element = self.wait_until_visible(
            '#project-created-notification', poll=3)

        self.assertTrue(project_name in element.text,
                        "New project name not in new project notification")

        self.assertTrue(Project.objects.filter(name=project_name).count(),
                        "New project not found in database")

    def test_new_duplicates_project_name(self):
        """
        Should not be able to create a new project whose name is the same
        as an existing project
       """

        project_name = "dupproject"

        Project.objects.create_project(name=project_name,
                                       release=self.release)

        url = reverse('newproject')
        self.get(url)
        self.wait_until_visible('#new-project-name', poll=3)

        self.enter_text('#new-project-name', project_name)

        select = Select(self.find('#projectversion'))
        select.select_by_value(str(self.release.pk))

        radio = self.driver.find_element(By.ID, 'type-new')
        radio.click()

        self.click("#create-project-button")

        self.wait_until_present('#hint-error-project-name', poll=3)
        element = self.find('#hint-error-project-name')

        self.assertTrue(("Project names must be unique" in element.text),
                        "Did not find unique project name error message")

        # Try and click it anyway, if it submits we'll have a new project in
        # the db and assert then
        try:
            self.click("#create-project-button")
        except InvalidElementStateException:
            pass

        self.assertTrue(
            (Project.objects.filter(name=project_name).count() == 1),
            "New project not found in database")
