import os
import shutil
import subprocess

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotDataVar, skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage

from oeqa.runtime.cases.dnf import DnfTest
from oeqa.utils.logparser import *
from oeqa.utils.httpserver import HTTPService

class PtestRunnerTest(DnfTest):

    @classmethod
    def setUpClass(cls):
        rpm_deploy = os.path.join(cls.tc.td['DEPLOY_DIR'], 'rpm')
        cls.repo_server = HTTPService(rpm_deploy, cls.tc.target.server_ip)
        cls.repo_server.start()

    @classmethod
    def tearDownClass(cls):
        cls.repo_server.stop()

    # a ptest log parser
    def parse_ptest(self, logfile):
        parser = Lparser(test_0_pass_regex="^PASS:(.+)",
                         test_0_fail_regex="^FAIL:(.+)",
                         section_0_begin_regex="^BEGIN: .*/(.+)/ptest",
                         section_0_end_regex="^END: .*/(.+)/ptest")
        parser.init()
        result = Result()

        with open(logfile, errors='replace') as f:
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

    def _install_ptest_packages(self):
        # Get ptest packages that can be installed in the image.
        packages_dir = os.path.join(self.tc.td['DEPLOY_DIR'], 'rpm')
        ptest_pkgs = [pkg[:pkg.find('-ptest')+6]
                          for _, _, filenames in os.walk(packages_dir)
                          for pkg in filenames
                          if 'ptest' in pkg
                          and pkg[:pkg.find('-ptest')] in self.tc.image_packages]

        repo_url = 'http://%s:%s' % (self.target.server_ip,
                                     self.repo_server.port)
        dnf_options = ('--repofrompath=oe-ptest-repo,%s '
                       '--nogpgcheck '
                       'install -y' % repo_url)
        self.dnf('%s %s ptest-runner' % (dnf_options, ' '.join(ptest_pkgs)))

    @skipIfNotFeature('package-management',
                      'Test requires package-management to be in DISTRO_FEATURES')
    @skipIfNotFeature('ptest',
                      'Test requires package-management to be in DISTRO_FEATURES')
    @skipIfNotDataVar('IMAGE_PKGTYPE', 'rpm',
                      'RPM is not the primary package manager')
    @OEHasPackage(['dnf'])
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_ptestrunner(self):
        self.ptest_log = os.path.join(self.tc.td['TEST_LOG_DIR'],
                                      'ptest-%s.log' % self.tc.td['DATETIME'])
        self._install_ptest_packages()

        (runnerstatus, result) = self.target.run('/usr/bin/ptest-runner > /tmp/ptest.log 2>&1', 0)
        #exit code is !=0 even if ptest-runner executes because some ptest tests fail.
        self.assertTrue(runnerstatus != 127, msg="Cannot execute ptest-runner!")
        self.target.copyFrom('/tmp/ptest.log', self.ptest_log)
        shutil.copyfile(self.ptest_log, "ptest.log")

        result = self.parse_ptest("ptest.log")
        log_results_to_location = "./results"
        if os.path.exists(log_results_to_location):
            shutil.rmtree(log_results_to_location)
        os.makedirs(log_results_to_location)

        result.log_as_files(log_results_to_location, test_status = ['pass','fail'])
