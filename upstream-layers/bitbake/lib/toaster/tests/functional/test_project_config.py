#! /usr/bin/env python3 #
# BitBake Toaster UI tests implementation
#
# Copyright (C) 2023 Savoir-faire Linux
#
# SPDX-License-Identifier: GPL-2.0-only
#

import string
import pytest
from django.urls import reverse
from selenium.webdriver import Keys
from selenium.webdriver.support.select import Select
from selenium.common.exceptions import TimeoutException
from tests.functional.functional_helpers import SeleniumFunctionalTestCase
from selenium.webdriver.common.by import By

from .utils import get_projectId_from_url

class TestProjectConfig(SeleniumFunctionalTestCase):
    project_id = None
    PROJECT_NAME = 'TestProjectConfig'
    INVALID_PATH_START_TEXT = 'The directory path should either start with a /'
    INVALID_PATH_CHAR_TEXT = 'The directory path cannot include spaces or ' \
        'any of these characters'

    def _get_config_nav_item(self, index):
        config_nav = self.find('#config-nav')
        return config_nav.find_elements(By.TAG_NAME, 'li')[index]

    def _navigate_bbv_page(self):
        """ Navigate to project BitBake variables page """
        # check if the menu is displayed
        if TestProjectConfig.project_id is None:
            TestProjectConfig.project_id = self.create_new_project(self.PROJECT_NAME, '3', None, True)

        url = reverse('projectconf', args=(TestProjectConfig.project_id,))
        self.get(url)
        self.wait_until_visible('#config-nav')
        bbv_page_link = self._get_config_nav_item(9)
        bbv_page_link.click()
        self.wait_until_visible('#config-nav')

    def test_no_underscore_iamgefs_type(self):
        """
        Should not accept IMAGEFS_TYPE with an underscore
        """
        self._navigate_bbv_page()
        imagefs_type = "foo_bar"

        self.wait_until_visible('#change-image_fstypes-icon')

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
        self._navigate_bbv_page()

        imagefs_type = "btrfs"

        self.wait_until_visible('#change-image_fstypes-icon')

        self.click('#change-image_fstypes-icon')

        self.enter_text('#new-imagefs_types', imagefs_type)

        checkboxes = self.driver.find_elements(By.XPATH, "//input[@class='fs-checkbox-fstypes']")

        for checkbox in checkboxes:
            if checkbox.get_attribute("value") == "btrfs":
               self.assertEqual(checkbox.is_selected(), True)

    def test_textbox_with_checkbox_verification(self):
        """
        Should automatically add or remove value in textbox, if user checks
        or unchecks checkboxes.
        """
        self._navigate_bbv_page()

        self.wait_until_visible('#change-image_fstypes-icon')
        self.click('#change-image_fstypes-icon')

        checkboxes_selector = '.fs-checkbox-fstypes'

        self.wait_until_visible(checkboxes_selector)
        checkboxes = self.find_all(checkboxes_selector)

        for checkbox in checkboxes:
            if checkbox.get_attribute("value") == "cpio":
               checkbox.click()
               self.wait_until_visible('#new-imagefs_types')
               element = self.driver.find_element(By.ID, 'new-imagefs_types')

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
        self._navigate_bbv_page()

        # activate the input to edit download dir
        try:
            change_dl_dir_btn = self.wait_until_visible('#change-dl_dir-icon')
        except TimeoutException:
            # If download dir is not displayed, test is skipped
            change_dl_dir_btn = None

        if change_dl_dir_btn:
            change_dl_dir_btn.click()

            # downloads dir path doesn't start with / or ${...}
            input_field = self.wait_until_visible('#new-dl_dir')
            input_field.clear()
            self.enter_text('#new-dl_dir', 'home/foo')
            element = self.wait_until_visible('#hintError-initialChar-dl_dir')

            msg = 'downloads directory path starts with invalid character but ' \
                'treated as valid'
            self.assertTrue((self.INVALID_PATH_START_TEXT in element.text), msg)

            # downloads dir path has a space
            self.driver.find_element(By.ID, 'new-dl_dir').clear()
            self.enter_text('#new-dl_dir', '/foo/bar a')

            element = self.wait_until_visible('#hintError-dl_dir')
            msg = 'downloads directory path characters invalid but treated as valid'
            self.assertTrue((self.INVALID_PATH_CHAR_TEXT in element.text), msg)

            # downloads dir path starts with ${...} but has a space
            self.driver.find_element(By.ID,'new-dl_dir').clear()
            self.enter_text('#new-dl_dir', '${TOPDIR}/down foo')

            element = self.wait_until_visible('#hintError-dl_dir')
            msg = 'downloads directory path characters invalid but treated as valid'
            self.assertTrue((self.INVALID_PATH_CHAR_TEXT in element.text), msg)

            # downloads dir path starts with /
            self.driver.find_element(By.ID,'new-dl_dir').clear()
            self.enter_text('#new-dl_dir', '/bar/foo')

            hidden_element = self.driver.find_element(By.ID,'hintError-dl_dir')
            self.assertEqual(hidden_element.is_displayed(), False,
                'downloads directory path valid but treated as invalid')

            # downloads dir path starts with ${...}
            self.driver.find_element(By.ID,'new-dl_dir').clear()
            self.enter_text('#new-dl_dir', '${TOPDIR}/down')

            hidden_element = self.driver.find_element(By.ID,'hintError-dl_dir')
            self.assertEqual(hidden_element.is_displayed(), False,
                'downloads directory path valid but treated as invalid')

    def test_set_sstate_dir(self):
        """
        Validate the allowed and disallowed types in the directory field for
        SSTATE_DIR
        """
        self._navigate_bbv_page()

        try:
            btn_chg_sstate_dir = self.wait_until_visible('#change-sstate_dir-icon')
            self.click('#change-sstate_dir-icon')
        except TimeoutException:
            # If sstate_dir is not displayed, test is skipped
            btn_chg_sstate_dir = None

        if btn_chg_sstate_dir:  # Skip continuation if sstate_dir is not displayed
            # path doesn't start with / or ${...}
            input_field = self.wait_until_visible('#new-sstate_dir')
            input_field.clear()
            self.enter_text('#new-sstate_dir', 'home/foo')
            element = self.wait_until_visible('#hintError-initialChar-sstate_dir')

            msg = 'sstate directory path starts with invalid character but ' \
                'treated as valid'
            self.assertTrue((self.INVALID_PATH_START_TEXT in element.text), msg)

            # path has a space
            self.driver.find_element(By.ID, 'new-sstate_dir').clear()
            self.enter_text('#new-sstate_dir', '/foo/bar a')

            element = self.wait_until_visible('#hintError-sstate_dir')
            msg = 'sstate directory path characters invalid but treated as valid'
            self.assertTrue((self.INVALID_PATH_CHAR_TEXT in element.text), msg)

            # path starts with ${...} but has a space
            self.driver.find_element(By.ID,'new-sstate_dir').clear()
            self.enter_text('#new-sstate_dir', '${TOPDIR}/down foo')

            element = self.wait_until_visible('#hintError-sstate_dir')
            msg = 'sstate directory path characters invalid but treated as valid'
            self.assertTrue((self.INVALID_PATH_CHAR_TEXT in element.text), msg)

            # path starts with /
            self.driver.find_element(By.ID,'new-sstate_dir').clear()
            self.enter_text('#new-sstate_dir', '/bar/foo')

            hidden_element = self.driver.find_element(By.ID, 'hintError-sstate_dir')
            self.assertEqual(hidden_element.is_displayed(), False,
                'sstate directory path valid but treated as invalid')

            # paths starts with ${...}
            self.driver.find_element(By.ID, 'new-sstate_dir').clear()
            self.enter_text('#new-sstate_dir', '${TOPDIR}/down')

            hidden_element = self.driver.find_element(By.ID, 'hintError-sstate_dir')
            self.assertEqual(hidden_element.is_displayed(), False,
                'sstate directory path valid but treated as invalid')

    def _change_bbv_value(self, **kwargs):
        var_name, field, btn_id, input_id, value, save_btn, *_ = kwargs.values()
        """ Change bitbake variable value """
        self._navigate_bbv_page()
        self.wait_until_visible(f'#{btn_id}')
        if kwargs.get('new_variable'):
            self.find(f"#{btn_id}").clear()
            self.enter_text(f"#{btn_id}", f"{var_name}")
        else:
            self.click(f'#{btn_id}')

        self.wait_until_visible(f'#{input_id}')

        if kwargs.get('is_select'):
            select = Select(self.find(f'#{input_id}'))
            select.select_by_visible_text(value)
        else:
            self.find(f"#{input_id}").clear()
            self.enter_text(f'#{input_id}', f'{value}')
        self.click(f'#{save_btn}')
        value_displayed = str(self.wait_until_visible(f'#{field}').text).lower()
        msg = f'{var_name} variable not changed'
        self.assertTrue(str(value).lower() in value_displayed, msg)

    def test_change_distro_var(self):
        """ Test changing distro variable """
        self._change_bbv_value(
            var_name='DISTRO',
            field='distro',
            btn_id='change-distro-icon',
            input_id='new-distro',
            value='poky-changed',
            save_btn="apply-change-distro",
        )

    def test_set_image_install_append_var(self):
        """ Test setting IMAGE_INSTALL:append variable """
        self._change_bbv_value(
            var_name='IMAGE_INSTALL:append',
            field='image_install',
            btn_id='change-image_install-icon',
            input_id='new-image_install',
            value='bash, apt, busybox',
            save_btn="apply-change-image_install",
        )

    def test_set_package_classes_var(self):
        """ Test setting PACKAGE_CLASSES variable """
        self._change_bbv_value(
            var_name='PACKAGE_CLASSES',
            field='package_classes',
            btn_id='change-package_classes-icon',
            input_id='package_classes-select',
            value='package_deb',
            save_btn="apply-change-package_classes",
            is_select=True,
        )

    def test_create_new_bbv(self):
        """ Test creating new bitbake variable """
        self._change_bbv_value(
            var_name='New_Custom_Variable',
            field='configvar-list',
            btn_id='variable',
            input_id='value',
            value='new variable value',
            save_btn="add-configvar-button",
            new_variable=True
        )
