#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

class StapTest(OERuntimeTestCase):
    @skipIfNotFeature('tools-profile', 'Test requires tools-profile to be in IMAGE_FEATURES')
    @OEHasPackage(['systemtap'])
    @OEHasPackage(['gcc-symlinks'])
    @OEHasPackage(['kernel-devsrc'])
    def test_stap(self):
        cmd = 'make -C /usr/src/kernel scripts prepare'
        status, output = self.target.run(cmd, 900)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

        cmd = 'stap -v --disable-cache -DSTP_NO_VERREL_CHECK -s1 -e \'probe oneshot { print("Hello, "); println("world!") }\''
        status, output = self.target.run(cmd, 900)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
        self.assertIn('Hello, world!', output, msg='\n'.join([cmd, output]))
