#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var

class PicolibcTest(OESelftestTestCase):

    def test_picolibc(self):
        compatible_machines = ['qemuarm', 'qemuarm64', 'qemuriscv32', 'qemuriscv64']
        machine = get_bb_var('MACHINE')
        if machine not in compatible_machines:
            self.skipTest('This test only works with machines : %s' % ' '.join(compatible_machines))
        self.write_config('TCLIBC = "picolibc"')
        bitbake("picolibc-helloworld")
