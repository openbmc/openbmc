#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfQemu

class Ethernet_Test(OERuntimeTestCase):

    @skipIfQemu()
    @OETestDepends(['ethernet_ip_connman.Ethernet_Test.test_set_virtual_ip'])  
    def test_get_ip_from_dhcp(self): 
        (status, output) = self.target.run("connmanctl services | grep -E '*AO Wired|*AR Wired' | awk '{print $3}'")
        self.assertEqual(status, 0, msg='No wired interfaces are detected, output: %s' % output)
        wired_interfaces = output
        
        (status, output) = self.target.run("ip route | grep default | awk '{print $3}'")
        self.assertEqual(status, 0, msg='Failed to retrieve the default gateway, output: %s' % output)
        default_gateway = output

        (status, output) = self.target.run("connmanctl config %s --ipv4 dhcp && sleep 2 && ping -c 5 %s" % (wired_interfaces,default_gateway))
        self.assertEqual(status, 0, msg='Failed to get dynamic IP address via DHCP in connmand, output: %s' % output)
