#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotInDataVar

class X32libTest(OERuntimeTestCase):

    @skipIfNotInDataVar('DEFAULTTUNE', 'x86-64-x32',
                        'DEFAULTTUNE is not set to x86-64-x32')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_x32_file(self):
        cmd = 'readelf -h /bin/ls | grep Class | grep ELF32'
        status1 = self.target.run(cmd)[0]
        cmd = 'readelf -h /bin/ls | grep Machine | grep X86-64'
        status2 = self.target.run(cmd)[0]
        msg = ("/bin/ls isn't an X86-64 ELF32 binary. readelf says: %s" % 
                self.target.run("readelf -h /bin/ls")[1])
        self.assertTrue(status1 == 0 and status2 == 0, msg=msg)
