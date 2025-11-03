from oeqa.runtime.case import OERuntimeTestCase


class SystemReadyFedoraUnattendedTest(OERuntimeTestCase):
    def setUp(self):
        super().setUp()
        self.console = self.target.DEFAULT_CONSOLE

    def test_fedora_unattended(self):
        # Turn on the FVP.
        self.target.transition('on')

        # Timeout value = elapsed time * 2; where elapsed time was collected
        # from the elapsed time in the log.do_testimage for each function after
        # the build is finished on the development machine.
        self.target.expect(self.console,
                           '  Booting `Install Fedora 39\'',
                           timeout=(2 * 60))
        bb.plain('Installation status: Loading the installer, kernel and initrd...')

        self.target.expect(self.console,
                           'Setting up the installation environment',
                           timeout=(2 * 60 * 60))
        bb.plain('Installation status: Setting up the installation environment...')

        self.target.expect(self.console,
                           'Installing the software',
                           timeout=(30 * 60))
        bb.plain('Installation status: Installing the software packages...')

        # Waiting to respond to the boot loader prompt error message.
        self.target.expect(self.console,
                           'Please respond \'yes\' or \'no\': ',
                           timeout=(16 * 60 * 60))
        self.target.sendline(self.console, 'yes')

        # Waiting till the installation is finished.
        self.target.expect(self.console, r'.*login: ', timeout=(5 * 60 * 60))
        bb.plain('Installation status: Fedora installation finished successfully.')
