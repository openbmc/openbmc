#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

import threading
import time

class TerminalTest(OERuntimeTestCase):

    @OEHasPackage(['matchbox-terminal'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_terminal_running(self):
        t_thread = threading.Thread(target=self.target.run, args=("export DISPLAY=:0 && matchbox-terminal -e 'sh -c \"uname -a && exec sh\"'",))
        t_thread.start()
        time.sleep(2)
        
        status, output = self.target.run('pidof matchbox-terminal')
        number_of_terminal = len(output.split())
        self.assertEqual(number_of_terminal, 1, msg='There should be only one terminal being launched. Number of terminal launched : %s' % number_of_terminal)
        self.target.run('kill -9 %s' % output)
        self.assertEqual(status, 0, msg='Not able to find process that runs terminal.')     
