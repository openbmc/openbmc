#! /usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

"""
Run the js unit tests
"""

from django.core.urlresolvers import reverse
from tests.browser.selenium_helpers import SeleniumTestCase
import logging

logger = logging.getLogger("toaster")


class TestJsUnitTests(SeleniumTestCase):
    """ Test landing page shows the Toaster brand """

    fixtures = ['toastergui-unittest-data']

    def test_that_js_unit_tests_pass(self):
        url = reverse('js-unit-tests')
        self.get(url)
        self.wait_until_present('#tests-failed')

        failed = self.find("#tests-failed").text
        passed = self.find("#tests-passed").text
        total = self.find("#tests-total").text

        logger.info("Js unit tests completed %s out of %s passed, %s failed",
                    passed,
                    total,
                    failed)

        failed_tests = self.find_all("li .fail .test-message")
        for fail in failed_tests:
            logger.error("JS unit test failed: %s" % fail.text)

        self.assertEqual(failed, '0',
                         "%s JS unit tests failed" % failed)
