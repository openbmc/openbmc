#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

class KernelModuleTest(OERuntimeTestCase):

    @classmethod
    def setUp(cls):
        src = os.path.join(cls.tc.runtime_files_dir, 'hellomod.c')
        dst = '/tmp/hellomod.c'
        cls.tc.target.copyTo(src, dst)

        src = os.path.join(cls.tc.runtime_files_dir, 'hellomod_makefile')
        dst = '/tmp/Makefile'
        cls.tc.target.copyTo(src, dst)

    @classmethod
    def tearDown(cls):
        files = '/tmp/Makefile /tmp/hellomod.c'
        cls.tc.target.run('rm %s' % files)

    @skipIfNotFeature('tools-sdk',
                      'Test requires tools-sdk to be in IMAGE_FEATURES')
    @OETestDepends(['gcc.GccCompileTest.test_gcc_compile'])
    @OEHasPackage(['kernel-devsrc'])
    @OEHasPackage(['make'])
    @OEHasPackage(['gcc'])
    def test_kernel_module(self):
        cmds = [
            'cd /usr/src/kernel && make scripts prepare',
            'cd /tmp && make',
            'cd /tmp && insmod hellomod.ko',
            'lsmod | grep hellomod',
            'dmesg | grep Hello',
            'rmmod hellomod', 'dmesg | grep "Cleaning up hellomod"'
            ]
        for cmd in cmds:
            status, output = self.target.run(cmd, 900)
            self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
