from oeqa.runtime.case import OERuntimeTestCase


class SystemReadyACSTest(OERuntimeTestCase):
    def setUp(self):
        self.console = self.td.get('ARM_SYSTEMREADY_ACS_CONSOLE')
        self.assertNotEqual(self.console, '',
                            msg='ARM_SYSTEMREADY_ACS_CONSOLE is not set')

    def test_acs(self):
        """
        The purpose of this test case is to detect any issues with the ACS
        tests themselves. The intention is to fail only if there is an issue
        running the tests, not if an ACS test fails
        """
        self.target.transition('on')
        # The tests finish on a root shell
        test_patterns = [r'([a-zA-Z0-9_ ]+): \[([a-zA-Z_ ]+)\]',
                         'ACS run is completed'] # Signifies successful completion

        while True:
            match_id = self.target.expect(self.console, test_patterns,
                                          timeout=10*60*60)
            if match_id == 0:
                # A test group's result has been printed
                matches = self.target.match(self.console)
                group_name = matches[1].decode().strip()
                status = matches[2].decode().strip()
                self.logger.info(f'Test Group ({group_name}): {status}')
            elif match_id == 1:
                break

        # Workaround to ensure the model syncs the log files to disk
        self.target.sendline(self.console, r'sync /mnt')
        self.target.expect(self.console, r'root@generic-arm64:~#')

        self.logger.info('Linux tests complete')
