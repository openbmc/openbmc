#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class GccCompileTest(OERuntimeTestCase):

    @classmethod
    def setUp(cls):
        dst = '/tmp/'
        src = os.path.join(cls.tc.files_dir, 'test.c')
        cls.tc.target.copyTo(src, dst)

        src = os.path.join(cls.tc.runtime_files_dir, 'testmakefile')
        cls.tc.target.copyTo(src, dst)

        src = os.path.join(cls.tc.files_dir, 'test.cpp')
        cls.tc.target.copyTo(src, dst)

    @classmethod
    def tearDown(cls):
        files = '/tmp/test.c /tmp/test.o /tmp/test /tmp/testmakefile'
        cls.tc.target.run('rm %s' % files)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['gcc'])
    def test_gcc_compile(self):
        status, output = self.target.run('gcc /tmp/test.c -o /tmp/test -lm')
        msg = 'gcc compile failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        status, output = self.target.run('/tmp/test')
        msg = 'running compiled file failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['g++'])
    def test_gpp_compile(self):
        status, output = self.target.run('g++ /tmp/test.c -o /tmp/test -lm')
        msg = 'g++ compile failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        status, output = self.target.run('/tmp/test')
        msg = 'running compiled file failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['g++'])
    def test_gpp2_compile(self):
        status, output = self.target.run('g++ /tmp/test.cpp -o /tmp/test -lm')
        msg = 'g++ compile failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        status, output = self.target.run('/tmp/test')
        msg = 'running compiled file failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['gcc'])
    @OEHasPackage(['make'])
    def test_make(self):
        status, output = self.target.run('cd /tmp; make -f testmakefile')
        msg = 'running make failed, output %s' % output
        self.assertEqual(status, 0, msg=msg)
