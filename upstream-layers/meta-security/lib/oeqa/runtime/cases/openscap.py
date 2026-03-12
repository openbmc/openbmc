# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class OpenscapTest(OERuntimeTestCase):

    @OEHasPackage(["openscap"])
    @OETestDepends(["ssh.SSHTest.test_ssh"])
    def test_openscap_basic(self):
        status, output = self.target.run("oscap -V")
        msg = (
            "`oscap -V` command does not work as expected. "
            "Status and output:%s and %s" % (status, output)
        )
        self.assertEqual(status, 0, msg=msg)

    @OEHasPackage(["openscap"])
    @OEHasPackage(["scap-security-guide"])
    @OETestDepends(["ssh.SSHTest.test_ssh"])
    def test_openscap_scan(self):
        SCAP_SOURCE = "/usr/share/xml/scap/ssg/content/ssg-openembedded-xccdf.xml"
        CPE_DICT = "/usr/share/xml/scap/ssg/content/ssg-openembedded-cpe-dictionary.xml"

        cmd = "oscap info --profiles %s" % SCAP_SOURCE
        status, output = self.target.run(cmd)
        msg = (
            "oscap info` command does not work as expected.\n"
            "Command: %s\n" % cmd + "Status and output:%s and %s" % (status, output)
        )
        self.assertEqual(status, 0, msg=msg)

        for p in output.split("\n"):
            profile = p.split(":")[0]
            cmd = "oscap xccdf eval --cpe %s --profile %s %s" % (
                CPE_DICT,
                profile,
                SCAP_SOURCE,
            )
            status, output = self.target.run(cmd)
            msg = (
                "`oscap xccdf eval` does not work as expected.\n"
                "Command: %s\n" % cmd + "Status and output:%s and %s" % (status, output)
            )
            self.assertNotEqual(status, 1, msg=msg)
