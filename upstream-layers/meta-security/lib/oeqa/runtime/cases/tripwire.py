# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class TripwireTest(OERuntimeTestCase):

    @OEHasPackage(['tripwire'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_tripwire_help(self):
        status, output = self.target.run('tripwire --help')
        msg = ('tripwire command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 8, msg = msg)

    @OETestDepends(['tripwire.TripwireTest.test_tripwire_help'])
    def test_tripwire_twinstall(self):
        status, output = self.target.run('/etc/tripwire/twinstall.sh')
        match = re.search('The database was successfully generated.', output)
        if not match:
            msg = ('/etc/tripwire/twinstall.sh failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['tripwire.TripwireTest.test_tripwire_twinstall'])
    def test_tripwire_twadmin(self):
        status, output = self.target.run('twadmin --create-cfgfile --cfgfile /etc/tripwire/twcfg.enc --site-keyfile /etc/tripwire/site.key -Q tripwire /etc/tripwire/twcfg.txt')
        status, output = self.target.run('twadmin --create-polfile --cfgfile /etc/tripwire/twcfg.enc --polfile /etc/tripwire/twpol.enc --site-keyfile /etc/tripwire/site.key -Q tripwire /etc/tripwire/twpol.txt')
        match = re.search('Wrote policy file: /etc/tripwire/twpol.enc', output)
        if not match:
            msg = ('twadmin --create-profile ; failed. '
               'Status and output:%s and %s' % (status, output))
            self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['tripwire.TripwireTest.test_tripwire_twadmin'])
    def test_tripwire_init(self):
        status, hostname = self.target.run('hostname')
        status, output = self.target.run('tripwire --init --cfgfile /etc/tripwire/twcfg.enc --polfile /etc/tripwire/tw.pol --site-keyfile /etc/tripwire/site.key --local-keyfile /etc/tripwire/%s-local.key -P tripwire' % hostname)
        match = re.search('The database was successfully generated.', output)
        if not match:
            msg = ('tripwire --init; Failed for host: %s. '
               'Status and output:%s and %s' % (hostname, status, output))
            self.assertEqual(status, 0, msg = msg)
