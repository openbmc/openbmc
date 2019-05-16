#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

from oeqa.core.case import OETestCase
from oeqa.utils.package_manager import install_package, uninstall_package

class OERuntimeTestCase(OETestCase):
    # target instance set by OERuntimeTestLoader.
    target = None

    def setUp(self):
        super(OERuntimeTestCase, self).setUp()
        install_package(self)

    def tearDown(self):
        super(OERuntimeTestCase, self).tearDown()
        uninstall_package(self)
