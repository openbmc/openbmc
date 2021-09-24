#
# SPDX-License-Identifier: MIT
#


from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class BitBakeLogging(OESelftestTestCase):

    def assertCount(self, item, entry, count):
        self.assertEqual(item.count(entry), count, msg="Output:\n'''\n%s\n'''\ndoesn't contain %d copies of:\n'''\n%s\n'''\n" % (item, count, entry))

    def test_shell_logging(self):
        # no logs, no verbose
        self.write_config('BBINCLUDELOGS = ""')
        result = bitbake("logging-test -c shelltest -f", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        self.assertNotIn("This is shell stdout", result.output)
        self.assertNotIn("This is shell stderr", result.output)

        # logs, no verbose
        self.write_config('BBINCLUDELOGS = "yes"')
        result = bitbake("logging-test -c shelltest -f", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        self.assertCount(result.output, "This is shell stdout", 1)
        self.assertCount(result.output, "This is shell stderr", 1)

        # no logs, verbose
        self.write_config('BBINCLUDELOGS = ""')
        result = bitbake("logging-test -c shelltest -f -v", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        # two copies due to set +x        
        self.assertCount(result.output, "This is shell stdout", 2)
        self.assertCount(result.output, "This is shell stderr", 2)

        # logs, verbose
        self.write_config('BBINCLUDELOGS = "yes"')
        result = bitbake("logging-test -c shelltest -f -v", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        # two copies due to set +x
        self.assertCount(result.output, "This is shell stdout", 2)
        self.assertCount(result.output, "This is shell stderr", 2)

    def test_python_exit_logging(self):
        # no logs, no verbose
        self.write_config('BBINCLUDELOGS = ""')
        result = bitbake("logging-test -c pythontest_exit -f", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        self.assertNotIn("This is python stdout", result.output)

        # logs, no verbose
        self.write_config('BBINCLUDELOGS = "yes"')
        result = bitbake("logging-test -c pythontest_exit -f", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        # A sys.exit() should include the output
        self.assertCount(result.output, "This is python stdout", 1)

        # no logs, verbose
        self.write_config('BBINCLUDELOGS = ""')
        result = bitbake("logging-test -c pythontest_exit -f -v", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        # python tasks don't log output with -v currently
        #self.assertCount(result.output, "This is python stdout", 1)

        # logs, verbose
        self.write_config('BBINCLUDELOGS = "yes"')
        result = bitbake("logging-test -c pythontest_exit -f -v", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        # python tasks don't log output with -v currently
        #self.assertCount(result.output, "This is python stdout", 1)

    def test_python_fatal_logging(self):
        # no logs, no verbose
        self.write_config('BBINCLUDELOGS = ""')
        result = bitbake("logging-test -c pythontest_fatal -f", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        self.assertNotIn("This is python fatal test stdout", result.output)
        self.assertCount(result.output, "This is a fatal error", 1)

        # logs, no verbose
        self.write_config('BBINCLUDELOGS = "yes"')
        result = bitbake("logging-test -c pythontest_fatal -f", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        # A bb.fatal() should not include the output
        self.assertNotIn("This is python fatal test stdout", result.output)
        self.assertCount(result.output, "This is a fatal error", 1)

        # no logs, verbose
        self.write_config('BBINCLUDELOGS = ""')
        result = bitbake("logging-test -c pythontest_fatal -f -v", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        # python tasks don't log output with -v currently
        #self.assertCount(result.output, "This is python fatal test stdout", 1)
        self.assertCount(result.output, "This is a fatal error", 1)

        # logs, verbose
        self.write_config('BBINCLUDELOGS = "yes"')
        result = bitbake("logging-test -c pythontest_fatal -f -v", ignore_status = True)
        self.assertIn("ERROR: Logfile of failure stored in:", result.output)
        # python tasks don't log output with -v currently
        #self.assertCount(result.output, "This is python fatal test stdout", 1)
        self.assertCount(result.output, "This is a fatal error", 1)

