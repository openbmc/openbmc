#
# SPDX-License-Identifier: MIT
#

import os, tempfile
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

            self._run('. %s/oe-init-build-env %s && bitbake virtual/libc' % (corebase, testdir))
