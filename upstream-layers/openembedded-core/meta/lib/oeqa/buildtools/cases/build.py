#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os, tempfile
import time
from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class BuildTests(OESDKTestCase):
    """
    Verify that bitbake can build virtual/libc inside the buildtools.
    """
    def test_libc(self):
        with tempfile.TemporaryDirectory(prefix='bitbake-build-', dir=self.tc.sdk_dir) as testdir:
            corebase = self.td['COREBASE']

            self._run('. %s/oe-init-build-env %s' % (corebase, testdir))
            with open(os.path.join(testdir, 'conf', 'local.conf'), 'ta') as conf:
                conf.write('\n')
                conf.write('DL_DIR = "%s"\n' % self.td['DL_DIR'])

            try:
                self._run('. %s/oe-init-build-env %s && bitbake virtual/libc' % (corebase, testdir))
            finally:
                delay = 10
                while delay and (os.path.exists(testdir + "/bitbake.lock") or os.path.exists(testdir + "/cache/hashserv.db-wal")):
                    time.sleep(1)
                    delay = delay - 1
