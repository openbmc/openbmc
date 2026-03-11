#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends

import time

class RunLevel_Test(OERuntimeTestCase):
    
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_runlevel_3(self):
        (status, output) = self.target.run("init 3 && sleep 5 && runlevel")
        runlevel= '5 3'
        self.assertEqual(output, runlevel, msg='Failed to set current runlevel to runlevel 3, current runlevel : %s' % output[-1])
        (status, output) = self.target.run("uname -a")
        self.assertEqual(status, 0, msg='Failed to run uname command, output: %s' % output)
        
    @OETestDepends(['runlevel.RunLevel_Test.test_runlevel_3']) 
    def test_runlevel_5(self):
        (status, output) = self.target.run("init 5 && sleep 5 && runlevel")
        runlevel = '3 5'
        self.assertEqual(output, runlevel, msg='Failed to set current runlevel to runlevel 5, current runlevel : %s' % output[-1])
        (status, output) = self.target.run('export DISPLAY=:0 && x11perf -aa10text')
        self.assertEqual(status, 0, msg='Failed to run 2D graphic test, output: %s' % output)
