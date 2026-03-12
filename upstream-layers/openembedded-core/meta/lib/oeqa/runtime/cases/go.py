#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class GoCompileTest(OERuntimeTestCase):

    @classmethod
    def setUp(cls):
        dst = '/tmp/'
        src = os.path.join(cls.tc.files_dir, 'test.go')
        cls.tc.target.copyTo(src, dst)

    @classmethod
    def tearDown(cls):
        files = '/tmp/test.go /tmp/test'
        cls.tc.target.run('rm %s' % files)
        dirs = '/tmp/hello-go'
        cls.tc.target.run('rm -r %s' % dirs)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage('go')
    @OEHasPackage('go-runtime')
    @OEHasPackage('go-runtime-dev')
    def test_go_compile(self):
        # Check if go is available
        status, output = self.target.run('which go')
        if status != 0:
            self.skipTest('go command not found, output: %s' % output)

        # Compile the simple Go program
        status, output = self.target.run('go build -o /tmp/test /tmp/test.go', 600)
        msg = 'go compile failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        # Run the compiled program
        status, output = self.target.run('/tmp/test')
        msg = 'running compiled file failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage('go')
    @OEHasPackage('go-runtime')
    @OEHasPackage('go-runtime-dev')
    def test_go_module(self):
        # Check if go is available
        status, output = self.target.run('which go')
        if status != 0:
            self.skipTest('go command not found, output: %s' % output)

        # Create a simple Go module
        status, output = self.target.run('mkdir -p /tmp/hello-go')
        msg = 'mkdir failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        # Copy the existing test.go file to the module
        status, output = self.target.run('cp /tmp/test.go /tmp/hello-go/main.go')
        msg = 'copying test.go failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        # Build the module
        status, output = self.target.run('cd /tmp/hello-go && go build -o hello main.go', 600)
        msg = 'go build failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        # Run the module
        status, output = self.target.run('cd /tmp/hello-go && ./hello')
        msg = 'running go module failed, output: %s' % output
        self.assertEqual(status, 0, msg=msg)

class GoHelloworldTest(OERuntimeTestCase):
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['go-helloworld'])
    def test_gohelloworld(self):
        cmd = "go-helloworld"
        status, output = self.target.run(cmd)
        msg = 'Exit status was not 0. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        msg = 'Incorrect output: %s' % output
        self.assertEqual(output, "Hello, world!", msg=msg)
