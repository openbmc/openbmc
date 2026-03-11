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
import re
import requests

from django.urls import reverse
from tests.browser.selenium_helpers_base import SeleniumTestCaseBase
from selenium.webdriver.common.by import By
from selenium.webdriver.support.select import Select
from selenium.common.exceptions import NoSuchElementException

logger = logging.getLogger("toaster")
toaster_processes = []

class SeleniumFunctionalTestCase(SeleniumTestCaseBase):
    wait_toaster_time = 10

    @classmethod
    def setUpClass(cls):
        # So that the buildinfo helper uses the test database'
        if os.environ.get('DJANGO_SETTINGS_MODULE', '') != \
            'toastermain.settings_test':
            raise RuntimeError("Please initialise django with the tests settings:  "
                "DJANGO_SETTINGS_MODULE='toastermain.settings_test'")

        # Wait for any known toaster processes to exit
        global toaster_processes
        for toaster_process in toaster_processes:
            try:
                os.waitpid(toaster_process, os.WNOHANG)
            except ChildProcessError:
                pass

        # start toaster
        cmd = "bash -c 'source toaster start'"
        start_process = subprocess.Popen(
            cmd,
            cwd=os.environ.get("BUILDDIR"),
            shell=True)
        toaster_processes = [start_process.pid]
        if start_process.wait() != 0:
            port_use = os.popen("lsof -i -P -n | grep '8000 (LISTEN)'").read().strip()
            message = ''
            if port_use:
                process_id = port_use.split()[1]
                process = os.popen(f"ps -o cmd= -p {process_id}").read().strip()
                message = f"Port 8000 occupied by {process}"
            raise RuntimeError(f"Can't initialize toaster. {message}")

        builddir = os.environ.get("BUILDDIR")
        with open(os.path.join(builddir, '.toastermain.pid'), 'r') as f:
            toaster_processes.append(int(f.read()))
        with open(os.path.join(builddir, '.runbuilds.pid'), 'r') as f:
            toaster_processes.append(int(f.read()))

        super(SeleniumFunctionalTestCase, cls).setUpClass()
        cls.live_server_url = 'http://localhost:8000/'

    @classmethod
    def tearDownClass(cls):
        super(SeleniumFunctionalTestCase, cls).tearDownClass()

        global toaster_processes

        cmd = "bash -c 'source toaster stop'"
        stop_process = subprocess.Popen(
            cmd,
            cwd=os.environ.get("BUILDDIR"),
            shell=True)
        # Toaster stop has been known to hang in these tests so force kill if it stalls
        try:
            if stop_process.wait(cls.wait_toaster_time) != 0:
                raise Exception('Toaster stop process failed')
        except Exception as e:
            if e is subprocess.TimeoutExpired:
                print('Toaster stop process took too long. Force killing toaster...')
            else:
                print('Toaster stop process failed. Force killing toaster...')
            stop_process.kill()
            for toaster_process in toaster_processes:
                os.kill(toaster_process, signal.SIGTERM)


    def get_URL(self):
         rc=self.get_page_source()
         project_url=re.search(r"(projectPageUrl\s:\s\")(.*)(\",)",rc)
         return project_url.group(2)


    def find_element_by_link_text_in_table(self, table_id, link_text):
        """
        Assume there're multiple suitable "find_element_by_link_text".
        In this circumstance we need to specify "table".
        """
        try:
            table_element = self.get_table_element(table_id)
            element = table_element.find_element(By.LINK_TEXT, link_text)
        except NoSuchElementException:
            print('no element found')
            raise
        return element

    def get_table_element(self, table_id, *coordinate):
        if len(coordinate) == 0:
#return whole-table element
            element_xpath = "//*[@id='" + table_id + "']"
            try:
                element = self.driver.find_element(By.XPATH, element_xpath)
            except NoSuchElementException:
                raise
            return element
        row = coordinate[0]

        if len(coordinate) == 1:
#return whole-row element
            element_xpath = "//*[@id='" + table_id + "']/tbody/tr[" + str(row) + "]"
            try:
                element = self.driver.find_element(By.XPATH, element_xpath)
            except NoSuchElementException:
                return False
            return element
#now we are looking for an element with specified X and Y
        column = coordinate[1]

        element_xpath = "//*[@id='" + table_id + "']/tbody/tr[" + str(row) + "]/td[" + str(column) + "]"
        try:
            element = self.driver.find_element(By.XPATH, element_xpath)
        except NoSuchElementException:
            return False
        return element

    def create_new_project(
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

        # Obtain a CSRF token from a suitable URL
        projs = requests.get(self.live_server_url + reverse('newproject'))
        csrftoken = projs.cookies.get('csrftoken')

        # Use the projects typeahead to find out if the project already exists
        req = requests.get(self.live_server_url + reverse('xhr_projectstypeahead'), {'search': project_name, 'format' : 'json'})
        data = req.json()
        # Delete any existing projects
        for result in data['results']:
            del_url = reverse('xhr_project', args=(result['id'],))
            del_response = requests.delete(self.live_server_url + del_url, cookies={'csrftoken': csrftoken}, headers={'X-CSRFToken': csrftoken})
            self.assertEqual(del_response.status_code, 200)

        self.get(reverse('newproject'))
        self.wait_until_visible('#new-project-name')
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

        self.wait_until_clickable('#create-project-button')

        self.driver.find_element(By.ID, "create-project-button").click()

        element = self.wait_until_visible('#project-created-notification')
        self.assertTrue(
            self.element_exists('#project-created-notification'),
            f"Project:{project_name} creation notification not shown"
        )
        self.assertTrue(
            project_name in element.text,
            f"New project name:{project_name} not in new project notification"
        )

        # Use the projects typeahead again to check the project now exists
        req = requests.get(self.live_server_url + reverse('xhr_projectstypeahead'), {'search': project_name, 'format' : 'json'})
        data = req.json()
        self.assertGreater(len(data['results']), 0, f"New project:{project_name} not found in database")

        project_id = data['results'][0]['id']

        self.wait_until_visible('#project-release-title')

        # check release
        if release_title is not None:
            self.assertTrue(re.search(
                release_title,
                self.driver.find_element(By.XPATH,
                                     "//span[@id='project-release-title']"
                                     ).text),
                            'The project release is not defined')

        return project_id
        
    def load_projects_page_helper(self):        
        self.wait_until_present('#projectstable')
        # Need to wait for some data in the table too
        self.wait_until_present('td[class="updated"]')

