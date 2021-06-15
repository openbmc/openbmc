# Copyright (C) 2019 Armin Kuster <akuster808@gmail.com>
#
import re
from tempfile import mkstemp

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class SuricataTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        cls.tmp_fd, cls.tmp_path = mkstemp()
        with os.fdopen(cls.tmp_fd, 'w') as f:
            # use google public dns
            f.write("nameserver 8.8.8.8")
            f.write(os.linesep)
            f.write("nameserver 8.8.4.4")
            f.write(os.linesep)
            f.write("nameserver 127.0.0.1")
            f.write(os.linesep)

    @classmethod
    def tearDownClass(cls):
        os.remove(cls.tmp_path)

    @OEHasPackage(['suricata'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_suricata_help(self):
        status, output = self.target.run('suricata --help')
        msg = ('suricata command does not work as expected. '
               'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 1, msg = msg)

    @OETestDepends(['suricata.SuricataTest.test_suricata_help'])
    def test_ping_openinfosecfoundation_org(self):
        dst = '/etc/resolv.conf'
        self.tc.target.run('rm -f %s' % dst)
        (status, output) = self.tc.target.copyTo(self.tmp_path, dst)
        msg = 'File could not be copied. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        status, output = self.target.run('ping -c 1 openinfosecfoundation.org')
        msg = ('ping openinfosecfoundation.org failed: output is:\n%s' % output)
        self.assertEqual(status, 0, msg = msg)

    @OEHasPackage(['python3-suricata-update'])
    @OETestDepends(['suricata.SuricataTest.test_ping_openinfosecfoundation_org'])
    def test_suricata_update(self):
        status, output = self.tc.target.run('suricata-update')
        msg = ('suricata-update had an unexpected failure. '
           'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['suricata.SuricataTest.test_suricata_update'])
    def test_suricata_update_sources_list(self):
        status, output = self.tc.target.run('suricata-update list-sources')
        msg = ('suricata-update list-sources had an unexpected failure. '
           'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['suricata.SuricataTest.test_suricata_update_sources_list'])
    def test_suricata_update_sources(self):
        status, output = self.tc.target.run('suricata-update update-sources')
        msg = ('suricata-update update-sources had an unexpected failure. '
           'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)

    @OETestDepends(['suricata.SuricataTest.test_suricata_update_sources'])
    def test_suricata_update_enable_source(self):
        status, output = self.tc.target.run('suricata-update enable-source oisf/trafficid')
        msg = ('suricata-update enable-source oisf/trafficid  had an unexpected failure. '
           'Status and output:%s and %s' % (status, output))
        self.assertEqual(status, 0, msg = msg)
