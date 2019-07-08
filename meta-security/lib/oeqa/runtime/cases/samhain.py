# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class SamhainTest(OERuntimeTestCase):

    @OEHasPackage(['samhain-standalone'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_samhain_standalone_help(self):
        status, output = self.target.run('samhain --help')
        match = re.search('Please report bugs to support@la-samhna.de.', output)
        if not match:
            msg = ('samhain-standalone command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 1, msg = msg)
