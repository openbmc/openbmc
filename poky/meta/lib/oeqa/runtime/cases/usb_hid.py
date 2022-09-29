#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfQemu
from oeqa.runtime.decorator.package import OEHasPackage

class USB_HID_Test(OERuntimeTestCase):

    def keyboard_mouse_simulation(self): 
        (status, output) = self.target.run('export DISPLAY=:0 && xdotool key F2 && xdotool mousemove 100 100')
        return self.assertEqual(status, 0,  msg = 'Failed to simulate keyboard/mouse input event, output : %s' % output)
             
    def set_suspend(self): 
        (status, output) = self.target.run('sudo rtcwake -m mem -s 10')
        return self.assertEqual(status, 0,  msg = 'Failed to suspends your system to RAM, output : %s' % output)
    
    @OEHasPackage(['xdotool'])
    @skipIfQemu()
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_USB_Hid_input(self):
        self.keyboard_mouse_simulation()
        self.set_suspend()
        self.keyboard_mouse_simulation()  
