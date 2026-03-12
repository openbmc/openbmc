#
# Copyright OpenEmbedded Contributors
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
        from oe.utils import parallel_make_value
        pmv = parallel_make_value((self.td.get('PARALLEL_MAKE') or '').split())
        parallel_make = "-j %d" % (pmv) if pmv else ""

        try:
            cmd = 'make %s -C /usr/src/kernel scripts prepare' % (parallel_make)
            status, output = self.target.run(cmd, 900)
            self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

            cmd = 'stap -v -p4 -m stap_hello --disable-cache -DSTP_NO_VERREL_CHECK -e \'probe oneshot { print("Hello, "); println("SystemTap!") }\''
            status, output = self.target.run(cmd, 900)
            self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

            cmd = 'staprun -v -R -b1 stap_hello.ko'
            status, output = self.target.run(cmd, 60)
            self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
            self.assertIn('Hello, SystemTap!', output, msg='\n'.join([cmd, output]))
        except:
            status, dmesg = self.target.run('dmesg')
            if status == 0:
                print(dmesg)
