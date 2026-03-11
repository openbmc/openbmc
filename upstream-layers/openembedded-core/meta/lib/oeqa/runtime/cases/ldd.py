#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

class LddTest(OERuntimeTestCase):

    @OEHasPackage(["ldd"])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_ldd(self):
        status, output = self.target.run('which ldd')
        msg = 'ldd does not exist in PATH: which ldd: %s' % output
        self.assertEqual(status, 0, msg=msg)

        cmd = ('for i in $(which ldd | xargs cat | grep "^RTLDLIST"| '
              'cut -d\'=\' -f2|tr -d \'"\'); '
              'do test -f $i && echo $i && break; done')
        status, output = self.target.run(cmd)
        self.assertEqual(status, 0, msg="ldd path not correct or RTLDLIST files don't exist.")

        status, output = self.target.run("ldd /bin/true")
        self.assertEqual(status, 0, msg="ldd failed to execute: %s" % output)
