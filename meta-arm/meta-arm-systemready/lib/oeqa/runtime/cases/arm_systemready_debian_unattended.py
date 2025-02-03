from oeqa.runtime.case import OERuntimeTestCase


class SystemReadyDebianUnattendedTest(OERuntimeTestCase):
    def setUp(self):
        super().setUp()
        self.console = self.target.DEFAULT_CONSOLE

    def test_debian_unattended(self):
        # Turn on the FVP.
        self.target.transition('on')

        # Timeout value = elapsed time * 2; where elapsed time was collected
        # from the elapsed time in the log.do_testimage for each function after
        # the build is finished on the development machine.
        self.target.expect(self.console,
                           r'.*Scanning installation',
                           timeout=(20 * 60))
        bb.plain('Installation status: Scanning installation media...')

        self.target.expect(self.console,
                           r'.*Detecting network hardware',
                           timeout=(35 * 60))
        bb.plain('Installation status: Detecting network hardware...')

        self.target.expect(self.console,
                           r'.*Installing the base system',
                           timeout=(35 * 60))
        bb.plain('Installation status: Installing the base system...')

        self.target.expect(self.console,
                           r'.*Installing GRUB',
                           timeout=(7 * 60 * 60))
        bb.plain('Installation status: Installing GRUB...')

        # Waiting to respond to the boot loader prompt error message.
        self.target.expect(self.console,
                           r'.*Press enter to continue',
                           timeout=(15 * 60))
        self.target.sendline(self.console, '')

        self.target.expect(self.console,
                           r'.*Press enter to continue',
                           timeout=(60))
        self.target.sendline(self.console, '')

        # Waiting to respond continue without bootloader to install
        # bootloader with the late script in preseed.cfg at the end.
        self.target.expect(self.console,
                           r'.*Continue without boot loader',
                           timeout=(2 * 60))
        self.target.sendline(self.console, '14')

        self.target.expect(self.console,
                           r'.*Press enter to continue',
                           timeout=(60))
        self.target.sendline(self.console, '')

        self.target.expect(self.console,
                           r'.*Finishing the installation',
                           timeout=(2 * 60))
        bb.plain('Installation status: Finishing the installation...')

        # Waiting till the installation is finished.
        self.target.expect(self.console, r'.*login:', timeout=(40 * 60))
        bb.plain('Installation status: Debian installation finished successfully.')
