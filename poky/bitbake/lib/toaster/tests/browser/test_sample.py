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
A small example test demonstrating the basics of writing a test with
Toaster's SeleniumTestCase; this just fetches the Toaster home page
and checks it has the word "Toaster" in the brand link

New test files should follow this structure, should be named "test_*.py",
and should be in the same directory as this sample.
"""

from django.core.urlresolvers import reverse
from tests.browser.selenium_helpers import SeleniumTestCase

class TestSample(SeleniumTestCase):
    """ Test landing page shows the Toaster brand """

    def test_landing_page_has_brand(self):
        url = reverse('landing')
        self.get(url)
        brand_link = self.find('.toaster-navbar-brand a.brand')
        self.assertEqual(brand_link.text.strip(), 'Toaster')
