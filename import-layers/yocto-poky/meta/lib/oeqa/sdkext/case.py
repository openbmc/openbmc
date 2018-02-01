# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import subprocess

from oeqa.utils import avoid_paths_in_environ
from oeqa.sdk.case import OESDKTestCase

class OESDKExtTestCase(OESDKTestCase):
    def _run(self, cmd):
        # extensible sdk shows a warning if found bitbake in the path
        # because can cause contamination, i.e. use devtool from
        # poky/scripts instead of eSDK one.
        env = os.environ.copy()
        paths_to_avoid = ['bitbake/bin', 'poky/scripts']
        env['PATH'] = avoid_paths_in_environ(paths_to_avoid)

        return subprocess.check_output(". %s > /dev/null;"\
            " %s;" % (self.tc.sdk_env, cmd), stderr=subprocess.STDOUT,
            shell=True, env=env, universal_newlines=True)
