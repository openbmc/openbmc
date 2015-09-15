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

    @testcase(223)
    def test_only_one_connmand_in_background(self):
        """
        Summary:     Only one connmand in background
        Expected:    There will be only one connmand instance in background.
        Product:     BSPs
        Author:      Alexandru Georgescu <alexandru.c.georgescu@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        # Make sure that 'connmand' is running in background
        (status, output) = self.target.run(oeRuntimeTest.pscmd + ' | grep [c]onnmand')
        self.assertEqual(0, status, 'Failed to find "connmand" process running in background.')

        # Start a new instance of 'connmand'
        (status, output) = self.target.run('connmand')
        self.assertEqual(0, status, 'Failed to start a new "connmand" process.')

        # Make sure that only one 'connmand' is running in background
        (status, output) = self.target.run(oeRuntimeTest.pscmd + ' | grep [c]onnmand | wc -l')
        self.assertEqual(0, status, 'Failed to find "connmand" process running in background.')
        self.assertEqual(1, int(output), 'Found {} connmand processes running, expected 1.'.format(output))
