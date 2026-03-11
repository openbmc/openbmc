#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# BitBake Toaster UI tests implementation
#
# Copyright (C) 2023 Savoir-faire Linux Inc
#
# SPDX-License-Identifier: GPL-2.0-only

import pytest
from django.urls import reverse
from selenium.webdriver.support.ui import Select
from tests.browser.selenium_helpers import SeleniumTestCase
from orm.models import BitbakeVersion, Project, Release
from selenium.webdriver.common.by import By

class TestDeleteProject(SeleniumTestCase):

    def setUp(self):
        bitbake, _ = BitbakeVersion.objects.get_or_create(
            name="master",
            giturl="git://master",
            branch="master",
            dirpath="master")

        self.release, _ = Release.objects.get_or_create(
            name="master",
            description="Yocto Project master",
            branch_name="master",
            helptext="latest",
            bitbake_version=bitbake)

        Release.objects.get_or_create(
            name="foo",
            description="Yocto Project foo",
            branch_name="foo",
            helptext="latest",
            bitbake_version=bitbake)

    @pytest.mark.django_db
    def test_delete_project(self):
        """ Test delete a project
            - Check delete modal is visible
            - Check delete modal has right text
            - Confirm delete
            - Check project is deleted
        """
        project_name = "project_to_delete"
        url = reverse('newproject')
        self.get(url)
        self.enter_text('#new-project-name', project_name)
        select = Select(self.find('#projectversion'))
        select.select_by_value(str(self.release.pk))
        self.click("#create-project-button")
        # We should get redirected to the new project's page with the
        # notification at the top
        element = self.wait_until_visible('#project-created-notification')
        self.assertTrue(project_name in element.text,
                        "New project name not in new project notification")
        self.assertTrue(Project.objects.filter(name=project_name).count(),
                        "New project not found in database")

        # Delete project
        delete_project_link = self.driver.find_element(
            By.XPATH, '//a[@href="#delete-project-modal"]')
        delete_project_link.click()
        
        # Check delete modal is visible
        self.wait_until_visible('#delete-project-modal')

        # Check delete modal has right text
        modal_header_text = self.find('#delete-project-modal .modal-header').text
        self.assertTrue(
            "Are you sure you want to delete this project?" in modal_header_text,
            "Delete project modal header text is wrong")

        modal_body_text = self.find('#delete-project-modal .modal-body').text
        self.assertTrue(
            "Cancel its builds currently in progress" in modal_body_text,
            "Modal body doesn't contain: Cancel its builds currently in progress")
        self.assertTrue(
            "Remove its configuration information" in modal_body_text,
            "Modal body doesn't contain: Remove its configuration information")
        self.assertTrue(
            "Remove its imported layers" in modal_body_text,
            "Modal body doesn't contain: Remove its imported layers")
        self.assertTrue(
            "Remove its custom images" in modal_body_text,
            "Modal body doesn't contain: Remove its custom images")
        self.assertTrue(
            "Remove all its build information" in modal_body_text,
            "Modal body doesn't contain: Remove all its build information")

        # Confirm delete
        delete_btn = self.find('#delete-project-confirmed')
        delete_btn.click()

        # Check project is deleted
        self.wait_until_visible('#change-notification')
        delete_notification = self.find('#change-notification-msg')
        self.assertTrue("You have deleted 1 project:" in delete_notification.text)
        self.assertTrue(project_name in delete_notification.text)
        self.assertFalse(Project.objects.filter(name=project_name).exists(),
                        "Project not deleted from database")
