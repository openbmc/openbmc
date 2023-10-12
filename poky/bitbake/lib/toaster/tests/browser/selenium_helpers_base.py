#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
# The Wait class and some of SeleniumDriverHelper and SeleniumTestCase are
# modified from Patchwork, released under the same licence terms as Toaster:
# https://github.com/dlespiau/patchwork/blob/master/patchwork/tests.browser.py

"""
Helper methods for creating Toaster Selenium tests which run within
the context of Django unit tests.
"""

import os
import time
import unittest

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
from selenium.common.exceptions import NoSuchElementException, \
        StaleElementReferenceException, TimeoutException

def create_selenium_driver(cls,browser='chrome'):
    # set default browser string based on env (if available)
    env_browser = os.environ.get('TOASTER_TESTS_BROWSER')
    if env_browser:
        browser = env_browser

    if browser == 'chrome':
        return webdriver.Chrome()
    elif browser == 'firefox':
        return webdriver.Firefox()
    elif browser == 'marionette':
        capabilities = DesiredCapabilities.FIREFOX
        capabilities['marionette'] = True
        return webdriver.Firefox(capabilities=capabilities)
    elif browser == 'ie':
        return webdriver.Ie()
    elif browser == 'phantomjs':
        return webdriver.PhantomJS()
    elif browser == 'remote':
        # if we were to add yet another env variable like TOASTER_REMOTE_BROWSER
        # we could let people pick firefox or chrome, left for later
        remote_hub= os.environ.get('TOASTER_REMOTE_HUB')
        driver = webdriver.Remote(remote_hub,
                                  webdriver.DesiredCapabilities.FIREFOX.copy())

        driver.get("http://%s:%s"%(cls.server_thread.host,cls.server_thread.port))
        return driver
    else:
        msg = 'Selenium driver for browser %s is not available' % browser
        raise RuntimeError(msg)

class Wait(WebDriverWait):
    """
    Subclass of WebDriverWait with predetermined timeout and poll
    frequency. Also deals with a wider variety of exceptions.
    """
    _TIMEOUT = 10
    _POLL_FREQUENCY = 0.5

    def __init__(self, driver):
        super(Wait, self).__init__(driver, self._TIMEOUT, self._POLL_FREQUENCY)

    def until(self, method, message=''):
        """
        Calls the method provided with the driver as an argument until the
        return value is not False.
        """

        end_time = time.time() + self._timeout
        while True:
            try:
                value = method(self._driver)
                if value:
                    return value
            except NoSuchElementException:
                pass
            except StaleElementReferenceException:
                pass

            time.sleep(self._poll)
            if time.time() > end_time:
                break

        raise TimeoutException(message)

    def until_not(self, method, message=''):
        """
        Calls the method provided with the driver as an argument until the
        return value is False.
        """

        end_time = time.time() + self._timeout
        while True:
            try:
                value = method(self._driver)
                if not value:
                    return value
            except NoSuchElementException:
                return True
            except StaleElementReferenceException:
                pass

            time.sleep(self._poll)
            if time.time() > end_time:
                break

        raise TimeoutException(message)

class SeleniumTestCaseBase(unittest.TestCase):
    """
    NB StaticLiveServerTestCase is used as the base test case so that
    static files are served correctly in a Selenium test run context; see
    https://docs.djangoproject.com/en/1.9/ref/contrib/staticfiles/#specialized-test-case-to-support-live-testing
    """

    @classmethod
    def setUpClass(cls):
        """ Create a webdriver driver at the class level """

        super(SeleniumTestCaseBase, cls).setUpClass()

        # instantiate the Selenium webdriver once for all the test methods
        # in this test case
        cls.driver = create_selenium_driver(cls)
        cls.driver.maximize_window()

    @classmethod
    def tearDownClass(cls):
        """ Clean up webdriver driver """

        cls.driver.quit()
        super(SeleniumTestCaseBase, cls).tearDownClass()

    def get(self, url):
        """
        Selenium requires absolute URLs, so convert Django URLs returned
        by resolve() or similar to absolute ones and get using the
        webdriver instance.

        url: a relative URL
        """
        abs_url = '%s%s' % (self.live_server_url, url)
        self.driver.get(abs_url)

    def find(self, selector):
        """ Find single element by CSS selector """
        return self.driver.find_element(By.CSS_SELECTOR, selector)

    def find_all(self, selector):
        """ Find all elements matching CSS selector """
        return self.driver.find_elements(By.CSS_SELECTOR, selector)

    def element_exists(self, selector):
        """
        Return True if one element matching selector exists,
        False otherwise
        """
        return len(self.find_all(selector)) == 1

    def focused_element(self):
        """ Return the element which currently has focus on the page """
        return self.driver.switch_to.active_element

    def wait_until_present(self, selector):
        """ Wait until element matching CSS selector is on the page """
        is_present = lambda driver: self.find(selector)
        msg = 'An element matching "%s" should be on the page' % selector
        element = Wait(self.driver).until(is_present, msg)
        return element

    def wait_until_visible(self, selector):
        """ Wait until element matching CSS selector is visible on the page """
        is_visible = lambda driver: self.find(selector).is_displayed()
        msg = 'An element matching "%s" should be visible' % selector
        Wait(self.driver).until(is_visible, msg)
        return self.find(selector)

    def wait_until_focused(self, selector):
        """ Wait until element matching CSS selector has focus """
        is_focused = \
            lambda driver: self.find(selector) == self.focused_element()
        msg = 'An element matching "%s" should be focused' % selector
        Wait(self.driver).until(is_focused, msg)
        return self.find(selector)

    def enter_text(self, selector, value):
        """ Insert text into element matching selector """
        # note that keyup events don't occur until the element is clicked
        # (in the case of <input type="text"...>, for example), so simulate
        # user clicking the element before inserting text into it
        field = self.click(selector)

        field.send_keys(value)
        return field

    def click(self, selector):
        """ Click on element which matches CSS selector """
        element = self.wait_until_visible(selector)
        element.click()
        return element

    def get_page_source(self):
        """ Get raw HTML for the current page """
        return self.driver.page_source
