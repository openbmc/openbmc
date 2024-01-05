#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

"""
A small example test demonstrating the basics of writing a test with
Toaster's SeleniumTestCase; this just fetches the Toaster home page
and checks it has the word "Toaster" in the brand link

New test files should follow this structure, should be named "test_*.py",
and should be in the same directory as this sample.
"""

from django.urls import reverse
from tests.browser.selenium_helpers import SeleniumTestCase

class TestSample(SeleniumTestCase):
    """ Test landing page shows the Toaster brand """

    def test_landing_page_has_brand(self):
        url = reverse('landing')
        self.get(url)
        brand_link = self.find('.toaster-navbar-brand a.brand')
        self.assertEqual(brand_link.text.strip(), 'Toaster')

    def test_no_builds_message(self):
        """ Test that a message is shown when there are no builds """
        url = reverse('all-builds')
        self.get(url)
        self.wait_until_visible('#empty-state-allbuildstable')  # wait for the empty state div to appear
        div_msg = self.find('#empty-state-allbuildstable .alert-info')

        msg = 'Sorry - no data found'
        self.assertEqual(div_msg.text, msg)
