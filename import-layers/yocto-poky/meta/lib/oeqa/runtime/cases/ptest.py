from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.utils.logparser import Lparser, Result

class PtestRunnerTest(OERuntimeTestCase):

    # a ptest log parser
    def parse_ptest(self, logfile):
        parser = Lparser(test_0_pass_regex="^PASS:(.+)",
                         test_0_fail_regex="^FAIL:(.+)",
                         test_0_skip_regex="^SKIP:(.+)",
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

                if line_type == 'test' and status == 'skip':
                    result.store(current_section, name, status)
                    continue

        result.sort_tests()
        return result

    @OETestID(1600)
    @skipIfNotFeature('ptest', 'Test requires ptest to be in DISTRO_FEATURES')
    @skipIfNotFeature('ptest-pkgs', 'Test requires ptest-pkgs to be in IMAGE_FEATURES')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_ptestrunner(self):
        import datetime

        test_log_dir = self.td.get('TEST_LOG_DIR', '')
        # The TEST_LOG_DIR maybe NULL when testimage is added after
        # testdata.json is generated.
        if not test_log_dir:
            test_log_dir = os.path.join(self.td.get('WORKDIR', ''), 'testimage')
        # Don't use self.td.get('DATETIME'), it's from testdata.json, not
        # up-to-date, and may cause "File exists" when re-reun.
        datetime = datetime.datetime.now().strftime('%Y%m%d%H%M%S')
        ptest_log_dir_link = os.path.join(test_log_dir, 'ptest_log')
        ptest_log_dir = '%s.%s' % (ptest_log_dir_link, datetime)
        ptest_runner_log = os.path.join(ptest_log_dir, 'ptest-runner.log')

        status, output = self.target.run('ptest-runner', 0)
        os.makedirs(ptest_log_dir)
        with open(ptest_runner_log, 'w') as f:
            f.write(output)

        # status != 0 is OK since some ptest tests may fail
        self.assertTrue(status != 127, msg="Cannot execute ptest-runner!")

        # Parse and save results
        parse_result = self.parse_ptest(ptest_runner_log)
        parse_result.log_as_files(ptest_log_dir, test_status = ['pass','fail', 'skip'])
        if os.path.exists(ptest_log_dir_link):
            # Remove the old link to create a new one
            os.remove(ptest_log_dir_link)
        os.symlink(os.path.basename(ptest_log_dir), ptest_log_dir_link)
