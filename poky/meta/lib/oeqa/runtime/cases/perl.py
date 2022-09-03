#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class PerlTest(OERuntimeTestCase):
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['perl'])
    def test_perl_works(self):
        status, output = self.target.run("perl -e '$_=\"Uryyb, jbeyq\"; tr/a-zA-Z/n-za-mN-ZA-M/;print'")
        self.assertEqual(status, 0)
        self.assertEqual(output, "Hello, world")
