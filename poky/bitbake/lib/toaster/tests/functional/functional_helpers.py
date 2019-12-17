#! /usr/bin/env python3
#
# BitBake Toaster functional tests implementation
#
# Copyright (C) 2017 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import logging
import subprocess
import signal
import time
import re

from tests.browser.selenium_helpers_base import SeleniumTestCaseBase
from tests.builds.buildtest import load_build_environment

logger = logging.getLogger("toaster")

class SeleniumFunctionalTestCase(SeleniumTestCaseBase):
    wait_toaster_time = 5

    @classmethod
    def setUpClass(cls):
        # So that the buildinfo helper uses the test database'
        if os.environ.get('DJANGO_SETTINGS_MODULE', '') != \
            'toastermain.settings_test':
            raise RuntimeError("Please initialise django with the tests settings:  " \
                "DJANGO_SETTINGS_MODULE='toastermain.settings_test'")

        load_build_environment()

        # start toaster
        cmd = "bash -c 'source toaster start'"
        p = subprocess.Popen(
            cmd,
            cwd=os.environ.get("BUILDDIR"),
            shell=True)
        if p.wait() != 0:
            raise RuntimeError("Can't initialize toaster")

        super(SeleniumFunctionalTestCase, cls).setUpClass()
        cls.live_server_url = 'http://localhost:8000/'

    @classmethod
    def tearDownClass(cls):
        super(SeleniumFunctionalTestCase, cls).tearDownClass()

        # XXX: source toaster stop gets blocked, to review why?
        # from now send SIGTERM by hand
        time.sleep(cls.wait_toaster_time)
        builddir = os.environ.get("BUILDDIR")

        with open(os.path.join(builddir, '.toastermain.pid'), 'r') as f:
            toastermain_pid = int(f.read())
            os.kill(toastermain_pid, signal.SIGTERM)
        with open(os.path.join(builddir, '.runbuilds.pid'), 'r') as f:
            runbuilds_pid = int(f.read())
            os.kill(runbuilds_pid, signal.SIGTERM)


    def get_URL(self):
         rc=self.get_page_source()
         project_url=re.search("(projectPageUrl\s:\s\")(.*)(\",)",rc)
         return project_url.group(2)


    def find_element_by_link_text_in_table(self, table_id, link_text):
        """
        Assume there're multiple suitable "find_element_by_link_text".
        In this circumstance we need to specify "table".
        """
        try:
            table_element = self.get_table_element(table_id)
            element = table_element.find_element_by_link_text(link_text)
        except NoSuchElementException as e:
            print('no element found')
            raise
        return element

    def get_table_element(self, table_id, *coordinate):
        if len(coordinate) == 0:
#return whole-table element
            element_xpath = "//*[@id='" + table_id + "']"
            try:
                element = self.driver.find_element_by_xpath(element_xpath)
            except NoSuchElementException as e:
                raise
            return element
        row = coordinate[0]

        if len(coordinate) == 1:
#return whole-row element
            element_xpath = "//*[@id='" + table_id + "']/tbody/tr[" + str(row) + "]"
            try:
                element = self.driver.find_element_by_xpath(element_xpath)
            except NoSuchElementException as e:
                return False
            return element
#now we are looking for an element with specified X and Y
        column = coordinate[1]

        element_xpath = "//*[@id='" + table_id + "']/tbody/tr[" + str(row) + "]/td[" + str(column) + "]"
        try:
            element = self.driver.find_element_by_xpath(element_xpath)
        except NoSuchElementException as e:
            return False
        return element
