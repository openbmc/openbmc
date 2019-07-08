#! /usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
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
from django.contrib.staticfiles.testing import StaticLiveServerTestCase
from tests.browser.selenium_helpers_base import SeleniumTestCaseBase

class SeleniumTestCase(SeleniumTestCaseBase, StaticLiveServerTestCase):
    pass
