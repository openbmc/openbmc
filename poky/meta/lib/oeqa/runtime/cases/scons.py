#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class SconsCompileTest(OERuntimeTestCase):

    @classmethod
    def setUp(cls):
        dst = '/tmp/'
        src = os.path.join(cls.tc.runtime_files_dir, 'hello.c')
        cls.tc.target.copyTo(src, dst)

        src = os.path.join(cls.tc.runtime_files_dir, 'SConstruct')
        cls.tc.target.copyTo(src, dst)

    @classmethod
    def tearDown(cls):
        files = '/tmp/hello.c /tmp/hello.o /tmp/hello /tmp/SConstruct'
        cls.tc.target.run('rm %s' % files)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['gcc'])
    @OEHasPackage(['python3-scons'])
    def test_scons_compile(self):
        status, output = self.target.run('cd /tmp/ && scons')
        msg = 'scons compile failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        status, output = self.target.run('/tmp/hello')
        msg = 'running compiled file failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)
