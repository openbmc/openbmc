#
# SPDX-License-Identifier: MIT
#

import tempfile
from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class BuildTests(OESDKTestCase):
    """
    Verify that our docs can build using our docs tools tarball.
    """
    def test_docs_build(self):
        with tempfile.TemporaryDirectory(prefix='docs-tarball-build-', dir=self.tc.sdk_dir) as testdir:
            self._run('git clone git://git.yoctoproject.org/yocto-docs %s' % testdir)
            self._run('cd %s/documentation && make html' % testdir)
