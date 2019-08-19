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

from orm.models import BitbakeVersion, Release, Project, ProjectVariable

class TestProjectConfigsPage(SeleniumTestCase):
    """ Test data at /project/X/builds is displayed correctly """

    PROJECT_NAME = 'test project'
    INVALID_PATH_START_TEXT = 'The directory path should either start with a /'
    INVALID_PATH_CHAR_TEXT = 'The directory path cannot include spaces or ' \
        'any of these characters'

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name='bbv1', giturl='/tmp/',
                                            branch='master', dirpath='')
        release = Release.objects.create(name='release1',
                                         bitbake_version=bbv)
        self.project1 = Project.objects.create_project(name=self.PROJECT_NAME,
                                                       release=release)
        self.project1.save()


    def test_no_underscore_iamgefs_type(self):
        """
        Should not accept IMAGEFS_TYPE with an underscore
        """

        imagefs_type = "foo_bar"

        ProjectVariable.objects.get_or_create(project = self.project1, name = "IMAGE_FSTYPES", value = "abcd ")
        url = reverse('projectconf', args=(self.project1.id,));
        self.get(url);

        self.click('#change-image_fstypes-icon')

        self.enter_text('#new-imagefs_types', imagefs_type)

        element = self.wait_until_visible('#hintError-image-fs_type')

        self.assertTrue(("A valid image type cannot include underscores" in element.text),
                        "Did not find underscore error message")


    def test_checkbox_verification(self):
        """
        Should automatically check the checkbox if user enters value
        text box, if value is there in the checkbox.
        """
        imagefs_type = "btrfs"

        ProjectVariable.objects.get_or_create(project = self.project1, name = "IMAGE_FSTYPES", value = "abcd ")
        url = reverse('projectconf', args=(self.project1.id,));
        self.get(url);

        self.click('#change-image_fstypes-icon')

        self.enter_text('#new-imagefs_types', imagefs_type)

        checkboxes = self.driver.find_elements_by_xpath("//input[@class='fs-checkbox-fstypes']")

        for checkbox in checkboxes:
            if checkbox.get_attribute("value") == "btrfs":
               self.assertEqual(checkbox.is_selected(), True)


    def test_textbox_with_checkbox_verification(self):
        """
        Should automatically add or remove value in textbox, if user checks
        or unchecks checkboxes.
        """

        ProjectVariable.objects.get_or_create(project = self.project1, name = "IMAGE_FSTYPES", value = "abcd ")
        url = reverse('projectconf', args=(self.project1.id,));
        self.get(url);

        self.click('#change-image_fstypes-icon')

        self.wait_until_visible('#new-imagefs_types')

        checkboxes_selector = '.fs-checkbox-fstypes'

        self.wait_until_visible(checkboxes_selector)
        checkboxes = self.find_all(checkboxes_selector)

        for checkbox in checkboxes:
            if checkbox.get_attribute("value") == "cpio":
               checkbox.click()
               element = self.driver.find_element_by_id('new-imagefs_types')

               self.wait_until_visible('#new-imagefs_types')

               self.assertTrue(("cpio" in element.get_attribute('value'),
                               "Imagefs not added into the textbox"))
               checkbox.click()
               self.assertTrue(("cpio" not in element.text),
                               "Image still present in the textbox")

    def test_set_download_dir(self):
        """
        Validate the allowed and disallowed types in the directory field for
        DL_DIR
        """

        ProjectVariable.objects.get_or_create(project=self.project1,
            name='DL_DIR')
        url = reverse('projectconf', args=(self.project1.id,))
        self.get(url)

        # activate the input to edit download dir
        self.click('#change-dl_dir-icon')
        self.wait_until_visible('#new-dl_dir')

        # downloads dir path doesn't start with / or ${...}
        self.enter_text('#new-dl_dir', 'home/foo')
        element = self.wait_until_visible('#hintError-initialChar-dl_dir')

        msg = 'downloads directory path starts with invalid character but ' \
            'treated as valid'
        self.assertTrue((self.INVALID_PATH_START_TEXT in element.text), msg)

        # downloads dir path has a space
        self.driver.find_element_by_id('new-dl_dir').clear()
        self.enter_text('#new-dl_dir', '/foo/bar a')

        element = self.wait_until_visible('#hintError-dl_dir')
        msg = 'downloads directory path characters invalid but treated as valid'
        self.assertTrue((self.INVALID_PATH_CHAR_TEXT in element.text), msg)

        # downloads dir path starts with ${...} but has a space
        self.driver.find_element_by_id('new-dl_dir').clear()
        self.enter_text('#new-dl_dir', '${TOPDIR}/down foo')

        element = self.wait_until_visible('#hintError-dl_dir')
        msg = 'downloads directory path characters invalid but treated as valid'
        self.assertTrue((self.INVALID_PATH_CHAR_TEXT in element.text), msg)

        # downloads dir path starts with /
        self.driver.find_element_by_id('new-dl_dir').clear()
        self.enter_text('#new-dl_dir', '/bar/foo')

        hidden_element = self.driver.find_element_by_id('hintError-dl_dir')
        self.assertEqual(hidden_element.is_displayed(), False,
            'downloads directory path valid but treated as invalid')

        # downloads dir path starts with ${...}
        self.driver.find_element_by_id('new-dl_dir').clear()
        self.enter_text('#new-dl_dir', '${TOPDIR}/down')

        hidden_element = self.driver.find_element_by_id('hintError-dl_dir')
        self.assertEqual(hidden_element.is_displayed(), False,
            'downloads directory path valid but treated as invalid')

    def test_set_sstate_dir(self):
        """
        Validate the allowed and disallowed types in the directory field for
        SSTATE_DIR
        """

        ProjectVariable.objects.get_or_create(project=self.project1,
            name='SSTATE_DIR')
        url = reverse('projectconf', args=(self.project1.id,))
        self.get(url)

        self.click('#change-sstate_dir-icon')

        self.wait_until_visible('#new-sstate_dir')

        # path doesn't start with / or ${...}
        self.enter_text('#new-sstate_dir', 'home/foo')
        element = self.wait_until_visible('#hintError-initialChar-sstate_dir')

        msg = 'sstate directory path starts with invalid character but ' \
            'treated as valid'
        self.assertTrue((self.INVALID_PATH_START_TEXT in element.text), msg)

        # path has a space
        self.driver.find_element_by_id('new-sstate_dir').clear()
        self.enter_text('#new-sstate_dir', '/foo/bar a')

        element = self.wait_until_visible('#hintError-sstate_dir')
        msg = 'sstate directory path characters invalid but treated as valid'
        self.assertTrue((self.INVALID_PATH_CHAR_TEXT in element.text), msg)

        # path starts with ${...} but has a space
        self.driver.find_element_by_id('new-sstate_dir').clear()
        self.enter_text('#new-sstate_dir', '${TOPDIR}/down foo')

        element = self.wait_until_visible('#hintError-sstate_dir')
        msg = 'sstate directory path characters invalid but treated as valid'
        self.assertTrue((self.INVALID_PATH_CHAR_TEXT in element.text), msg)

        # path starts with /
        self.driver.find_element_by_id('new-sstate_dir').clear()
        self.enter_text('#new-sstate_dir', '/bar/foo')

        hidden_element = self.driver.find_element_by_id('hintError-sstate_dir')
        self.assertEqual(hidden_element.is_displayed(), False,
            'sstate directory path valid but treated as invalid')

        # paths starts with ${...}
        self.driver.find_element_by_id('new-sstate_dir').clear()
        self.enter_text('#new-sstate_dir', '${TOPDIR}/down')

        hidden_element = self.driver.find_element_by_id('hintError-sstate_dir')
        self.assertEqual(hidden_element.is_displayed(), False,
            'sstate directory path valid but treated as invalid')