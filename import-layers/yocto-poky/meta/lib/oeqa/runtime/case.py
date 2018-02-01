# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.case import OETestCase
from oeqa.utils.package_manager import install_package, uninstall_package

class OERuntimeTestCase(OETestCase):
    # target instance set by OERuntimeTestLoader.
    target = None

    def _oeSetUp(self):
        super(OERuntimeTestCase, self)._oeSetUp()
        install_package(self)

    def _oeTearDown(self):
        super(OERuntimeTestCase, self)._oeTearDown()
        uninstall_package(self)
