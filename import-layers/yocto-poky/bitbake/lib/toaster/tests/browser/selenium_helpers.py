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
#
# The Wait class and some of SeleniumDriverHelper and SeleniumTestCase are
# modified from Patchwork, released under the same licence terms as Toaster:
# https://github.com/dlespiau/patchwork/blob/master/patchwork/tests.browser.py

"""
Helper methods for creating Toaster Selenium tests which run within
the context of Django unit tests.
"""
from django.contrib.staticfiles.testing import StaticLiveServerTestCase
from tests.browser.selenium_helpers_base import SeleniumTestCaseBase

class SeleniumTestCase(SeleniumTestCaseBase, StaticLiveServerTestCase):
    pass
