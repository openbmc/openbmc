import unittest, os, shutil
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *
from oeqa.utils.logparser import *
from oeqa.utils.httpserver import HTTPService
import bb
import glob
from oe.package_manager import RpmPkgsList
import subprocess

def setUpModule():
    if not oeRuntimeTest.hasFeature("package-management"):
        skipModule("Image doesn't have package management feature")
    if not oeRuntimeTest.hasPackage("smart"):
        skipModule("Image doesn't have smart installed")
    if "package_rpm" != oeRuntimeTest.tc.d.getVar("PACKAGE_CLASSES", True).split()[0]:
        skipModule("Rpm is not the primary package manager")

class PtestRunnerTest(oeRuntimeTest):

    # a ptest log parser
    def parse_ptest(self, logfile):
        parser = Lparser(test_0_pass_regex="^PASS:(.+)", test_0_fail_regex="^FAIL:(.+)", section_0_begin_regex="^BEGIN: .*/(.+)/ptest", section_0_end_regex="^END: .*/(.+)/ptest")
        parser.init()
        result = Result()

        with open(logfile) as f:
            for line in f:
                result_tuple = parser.parse_line(line)
                if not result_tuple:
                    continue
                result_tuple = line_type, category, status, name = parser.parse_line(line)

                if line_type == 'section' and status == 'begin':
                    current_section = name
                    continue

                if line_type == 'section' and status == 'end':
                    current_section = None
                    continue

                if line_type == 'test' and status == 'pass':
                    result.store(current_section, name, status)
                    continue

                if line_type == 'test' and status == 'fail':
                    result.store(current_section, name, status)
                    continue

        result.sort_tests()
        return result

    @classmethod
    def setUpClass(self):
        #note the existing channels that are on the board before creating new ones
#        self.existingchannels = set()
#        (status, result) = oeRuntimeTest.tc.target.run('smart channel --show | grep "\["', 0)
#        for x in result.split("\n"):
#            self.existingchannels.add(x)
        self.repo_server = HTTPService(oeRuntimeTest.tc.d.getVar('DEPLOY_DIR', True), oeRuntimeTest.tc.target.server_ip)
        self.repo_server.start()

    @classmethod
    def tearDownClass(self):
        self.repo_server.stop()
        #remove created channels to be able to repeat the tests on same image
#        (status, result) = oeRuntimeTest.tc.target.run('smart channel --show | grep "\["', 0)
#        for x in result.split("\n"):
#            if x not in self.existingchannels:
#                oeRuntimeTest.tc.target.run('smart channel --remove '+x[1:-1]+' -y', 0)

    def add_smart_channel(self):
        image_pkgtype = self.tc.d.getVar('IMAGE_PKGTYPE', True)
        deploy_url = 'http://%s:%s/%s' %(self.target.server_ip, self.repo_server.port, image_pkgtype)
        pkgarchs = self.tc.d.getVar('PACKAGE_ARCHS', True).replace("-","_").split()
        for arch in os.listdir('%s/%s' % (self.repo_server.root_dir, image_pkgtype)):
            if arch in pkgarchs:
                self.target.run('smart channel -y --add {a} type=rpm-md baseurl={u}/{a}'.format(a=arch, u=deploy_url), 0)
        self.target.run('smart update', 0)

    def install_complementary(self, globs=None):
        installed_pkgs_file = os.path.join(oeRuntimeTest.tc.d.getVar('WORKDIR', True),
                                           "installed_pkgs.txt")
        self.pkgs_list = RpmPkgsList(oeRuntimeTest.tc.d, oeRuntimeTest.tc.d.getVar('IMAGE_ROOTFS', True), oeRuntimeTest.tc.d.getVar('arch_var', True), oeRuntimeTest.tc.d.getVar('os_var', True))
        with open(installed_pkgs_file, "w+") as installed_pkgs:
            installed_pkgs.write(self.pkgs_list.list("arch"))

        cmd = [bb.utils.which(os.getenv('PATH'), "oe-pkgdata-util"),
               "-p", oeRuntimeTest.tc.d.getVar('PKGDATA_DIR', True), "glob", installed_pkgs_file,
               globs]
        try:
            bb.note("Installing complementary packages ...")
            complementary_pkgs = subprocess.check_output(cmd, stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            bb.fatal("Could not compute complementary packages list. Command "
                     "'%s' returned %d:\n%s" %
                     (' '.join(cmd), e.returncode, e.output))

        return complementary_pkgs.split()

    def setUpLocal(self):
        self.ptest_log = os.path.join(oeRuntimeTest.tc.d.getVar("TEST_LOG_DIR",True), "ptest-%s.log" % oeRuntimeTest.tc.d.getVar('DATETIME', True))

    @skipUnlessPassed('test_ssh')
    def test_ptestrunner(self):
        self.add_smart_channel()
        (runnerstatus, result) = self.target.run('which ptest-runner', 0)
        cond = oeRuntimeTest.hasPackage("ptest-runner") and oeRuntimeTest.hasFeature("ptest") and oeRuntimeTest.hasPackage("-ptest") and (runnerstatus != 0)
        if cond:
            self.install_packages(self.install_complementary("*-ptest"))
            self.install_packages(['ptest-runner'])

        (runnerstatus, result) = self.target.run('/usr/bin/ptest-runner > /tmp/ptest.log 2>&1', 0)
        #exit code is !=0 even if ptest-runner executes because some ptest tests fail.
        self.assertTrue(runnerstatus != 127, msg="Cannot execute ptest-runner!")
        self.target.copy_from('/tmp/ptest.log', self.ptest_log)
        shutil.copyfile(self.ptest_log, "ptest.log")

        result = self.parse_ptest("ptest.log")
        log_results_to_location = "./results"
        if os.path.exists(log_results_to_location):
            shutil.rmtree(log_results_to_location)
        os.makedirs(log_results_to_location)

        result.log_as_files(log_results_to_location, test_status = ['pass','fail'])
