# This test should cover https://bugzilla.yoctoproject.org/tr_show_case.cgi?case_id=289 testcase
# Note that the image under test must have logrotate installed

import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasPackage("logrotate"):
        skipModule("No logrotate package in image")


class LogrotateTest(oeRuntimeTest):

    @skipUnlessPassed("test_ssh")
    def test_1_logrotate_setup(self):
        (status, output) = self.target.run('mkdir /home/root/logrotate_dir')
        self.assertEqual(status, 0, msg = "Could not create logrotate_dir. Output: %s" % output)
        (status, output) = self.target.run("sed -i 's#wtmp {#wtmp {\\n    olddir /home/root/logrotate_dir#' /etc/logrotate.conf")
        self.assertEqual(status, 0, msg = "Could not write to logrotate.conf file. Status and output: %s and %s)" % (status, output))

    @testcase(289)
    @skipUnlessPassed("test_1_logrotate_setup")
    def test_2_logrotate(self):
        (status, output) = self.target.run('logrotate -f /etc/logrotate.conf')
        self.assertEqual(status, 0, msg = "logrotate service could not be reloaded. Status and output: %s and %s" % (status, output))
        output = self.target.run('ls -la /home/root/logrotate_dir/ | wc -l')[1]
        self.assertTrue(int(output)>=3, msg = "new logfile could not be created. List of files within log directory: %s" %(self.target.run('ls -la /home/root/logrotate_dir')[1]))
