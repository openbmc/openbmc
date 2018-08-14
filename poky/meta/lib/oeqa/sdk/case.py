# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import subprocess

from oeqa.core.case import OETestCase

class OESDKTestCase(OETestCase):
    def _run(self, cmd):
        return subprocess.check_output(". %s > /dev/null; %s;" % \
                (self.tc.sdk_env, cmd), shell=True,
                stderr=subprocess.STDOUT, universal_newlines=True)
