#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
from oeqa.sdk.context import OESDKTestContext, OESDKTestContextExecutor

class OESDKExtTestContext(OESDKTestContext):
    esdk_files_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "files")

    # FIXME - We really need to do better mapping of names here, this at
    # least allows some tests to run
    def hasHostPackage(self, pkg):
        # We force a toolchain to be installed into the eSDK even if its minimal
        if pkg.startswith("packagegroup-cross-canadian-"):
            return True
        return self._hasPackage(self.host_pkg_manifest, pkg)

class OESDKExtTestContextExecutor(OESDKTestContextExecutor):
    _context_class = OESDKExtTestContext

    name = 'esdk'
    help = 'esdk test component'
    description = 'executes esdk tests'

    default_cases = OESDKTestContextExecutor.default_cases + \
            [os.path.join(os.path.abspath(os.path.dirname(__file__)), 'cases')]
    default_test_data = None

_executor_class = OESDKExtTestContextExecutor
