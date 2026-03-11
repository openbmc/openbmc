#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotInDataVar

import subprocess

class X32libTest(OERuntimeTestCase):

    @skipIfNotInDataVar('DEFAULTTUNE', 'x86-64-x32',
                        'DEFAULTTUNE is not set to x86-64-x32')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_x32_file(self):
        dest = self.td.get('T', '') + "/ls.x32test"
        self.target.copyFrom("/bin/ls", dest)
        cmd = 'readelf -h {} | grep Class | grep ELF32'.format(dest)
        status1 = subprocess.call(cmd, shell=True)
        cmd = 'readelf -h {} | grep Machine | grep X86-64'.format(dest)
        status2 = subprocess.call(cmd, shell=True)
        msg = ("/bin/ls isn't an X86-64 ELF32 binary. readelf says:\n{}".format(
                subprocess.check_output("readelf -h {}".format(dest), shell=True).decode()))
        os.remove(dest)
        self.assertTrue(status1 == 0 and status2 == 0, msg=msg)
