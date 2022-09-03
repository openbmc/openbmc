#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfQemu

class Ethernet_Test(OERuntimeTestCase):

    def set_ip(self, x): 
        x = x.split(".")
        sample_host_address = '150'        
        x[3] = sample_host_address
        x = '.'.join(x)
        return x
    
    @skipIfQemu()
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_set_virtual_ip(self):
        (status, output) = self.target.run("ifconfig eth0 | grep 'inet ' | awk '{print $2}'")
        self.assertEqual(status, 0, msg='Failed to get ip address. Make sure you have an ethernet connection on your device, output: %s' % output)
        original_ip = output 
        virtual_ip = self.set_ip(original_ip)
        
        (status, output) = self.target.run("ifconfig eth0:1 %s netmask 255.255.255.0 && sleep 2 && ping -c 5 %s && ifconfig eth0:1 down" % (virtual_ip,virtual_ip))
        self.assertEqual(status, 0, msg='Failed to create virtual ip address, output: %s' % output)
        
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