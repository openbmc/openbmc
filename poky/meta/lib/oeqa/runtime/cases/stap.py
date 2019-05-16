#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

class StapTest(OERuntimeTestCase):

    @classmethod
    def setUp(cls):
        src = os.path.join(cls.tc.runtime_files_dir, 'hello.stp')
        dst = '/tmp/hello.stp'
        cls.tc.target.copyTo(src, dst)

    @classmethod
    def tearDown(cls):
        files = '/tmp/hello.stp'
        cls.tc.target.run('rm %s' % files)

    @skipIfNotFeature('tools-profile',
                      'Test requires tools-profile to be in IMAGE_FEATURES')
    @OETestDepends(['kernelmodule.KernelModuleTest.test_kernel_module'])
    @OEHasPackage(['systemtap'])
    def test_stap(self):
        cmds = [
            'cd /usr/src/kernel && make scripts prepare',
            'cd /lib/modules/`uname -r` && (if [ ! -e build ]; then ln -s /usr/src/kernel build; fi)',
            'stap --disable-cache -DSTP_NO_VERREL_CHECK /tmp/hello.stp'
            ]
        for cmd in cmds:
            status, output = self.target.run(cmd, 900)
            self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
