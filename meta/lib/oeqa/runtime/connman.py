import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasPackage("connman"):
        skipModule("No connman package in image")


class ConnmanTest(oeRuntimeTest):

    def service_status(self, service):
        if oeRuntimeTest.hasFeature("systemd"):
            (status, output) = self.target.run('systemctl status -l %s' % service)
            return output
        else:
            return "Unable to get status or logs for %s" % service

    @testcase(961)
    @skipUnlessPassed('test_ssh')
    def test_connmand_help(self):
        (status, output) = self.target.run('/usr/sbin/connmand --help')
        self.assertEqual(status, 0, msg="status and output: %s and %s" % (status,output))

    @testcase(221)
    @skipUnlessPassed('test_connmand_help')
    def test_connmand_running(self):
        (status, output) = self.target.run(oeRuntimeTest.pscmd + ' | grep [c]onnmand')
        if status != 0:
            print self.service_status("connman")
            self.fail("No connmand process running")
