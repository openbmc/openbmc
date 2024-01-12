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

import pytest
from selenium import webdriver
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
from selenium.common.exceptions import NoSuchElementException, \
        StaleElementReferenceException, TimeoutException, \
        SessionNotCreatedException

def create_selenium_driver(cls,browser='chrome'):
    # set default browser string based on env (if available)
    env_browser = os.environ.get('TOASTER_TESTS_BROWSER')
    if env_browser:
        browser = env_browser

    if browser == 'chrome':
        options = webdriver.ChromeOptions()
        options.add_argument('--headless')
        options.add_argument('--disable-infobars')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--no-sandbox')
        options.add_argument('--remote-debugging-port=9222')
        try:
            return webdriver.Chrome(options=options)
        except SessionNotCreatedException as e:
            exit_message = "Halting tests prematurely to avoid cascading errors."
            # check if chrome / chromedriver exists
            chrome_path = os.popen("find ~/.cache/selenium/chrome/ -name 'chrome' -type f -print -quit").read().strip()
            if not chrome_path:
                pytest.exit(f"Failed to install/find chrome.\n{exit_message}")
            chromedriver_path = os.popen("find ~/.cache/selenium/chromedriver/ -name 'chromedriver' -type f -print -quit").read().strip()
            if not chromedriver_path:
                pytest.exit(f"Failed to install/find chromedriver.\n{exit_message}")
            # check if depends on each are fulfilled
            depends_chrome = os.popen(f"ldd {chrome_path} | grep 'not found'").read().strip()
            if depends_chrome:
                pytest.exit(f"Missing chrome dependencies.\n{depends_chrome}\n{exit_message}")
            depends_chromedriver = os.popen(f"ldd {chromedriver_path} | grep 'not found'").read().strip()
            if depends_chromedriver:
                pytest.exit(f"Missing chromedriver dependencies.\n{depends_chromedriver}\n{exit_message}")
            # print original error otherwise
            pytest.exit(f"Failed to start chromedriver.\n{e}\n{exit_message}")
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

    def __init__(self, driver, timeout=_TIMEOUT, poll=_POLL_FREQUENCY):
        self._TIMEOUT = timeout
        self._POLL_FREQUENCY = poll
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
        # Allow driver resources to be properly freed before proceeding with further tests
        time.sleep(5)
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

        try:  # Ensure page is loaded before proceeding
            self.wait_until_visible("#global-nav", poll=3)
        except NoSuchElementException:
            self.driver.implicitly_wait(3)
        except TimeoutException:
            self.driver.implicitly_wait(3)

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

    def wait_until_present(self, selector, poll=0.5):
        """ Wait until element matching CSS selector is on the page """
        is_present = lambda driver: self.find(selector)
        msg = 'An element matching "%s" should be on the page' % selector
        element = Wait(self.driver, poll=poll).until(is_present, msg)
        if poll > 2:
            time.sleep(poll)  # element need more delay to be present
        return element

    def wait_until_visible(self, selector, poll=1):
        """ Wait until element matching CSS selector is visible on the page """
        is_visible = lambda driver: self.find(selector).is_displayed()
        msg = 'An element matching "%s" should be visible' % selector
        Wait(self.driver, poll=poll).until(is_visible, msg)
        time.sleep(poll)  # wait for visibility to settle
        return self.find(selector)

    def wait_until_clickable(self, selector, poll=1):
        """ Wait until element matching CSS selector is visible on the page """
        WebDriverWait(
            self.driver,
            Wait._TIMEOUT,
            poll_frequency=poll
        ).until(
            EC.element_to_be_clickable((By.ID, selector.removeprefix('#')
                                        )
                                       )
        )
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
