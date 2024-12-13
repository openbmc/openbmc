from oeqa.runtime.case import OERuntimeTestCase

class SystemReadyOpenSUSEUnattendedTest(OERuntimeTestCase):
    def setUp(self):
        super().setUp()
        self.console = self.target.DEFAULT_CONSOLE

    def test_opensuse_unattended(self):
        # Turn on the FVP.
        self.target.transition('on')

        # Timeout value = elapsed time * 2; where elapsed time was collected
        # from the elapsed time in the log.do_testimage for each function after
        # the build is finished on the development machine.
        self.target.expect(self.console,
                           'Booting `Installation\'',
                           timeout=(2 * 60))
        bb.plain('Installation status: Loading the kernel, initrd and basic drivers...')

        self.target.expect(self.console,
                           'Starting hardware detection...',
                           timeout=(40 * 60))
        bb.plain('Installation status: Starting hardware detection...')

        self.target.expect(self.console,
                           'Loading Installation System',
                           timeout=(60 * 60))
        bb.plain('Installation status: Loading Installation System...')

        self.target.expect(self.console,
                           'Starting Installer',
                           timeout=(40 * 60))
        bb.plain('Installation status: Performing Installation...')

        self.target.expect(self.console,
                           'Finishing Configuration',
                           timeout=(15 * 60 * 60))
        bb.plain('Installation status: Finishing Configuration...')

        # Waiting till the installation is finished.
        self.target.expect(self.console, r'.*login: ', timeout=(6 * 60 * 60))
        bb.plain('Installation status: openSUSE installation finished successfully.')
