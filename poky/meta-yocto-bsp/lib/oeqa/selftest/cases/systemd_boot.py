import os

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.depends import OETestDepends
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, runqemu

class Systemdboot(OESelftestTestCase):
    def _common_setup(self):
        """
        Common setup for test cases: 1445, 1528
        """

        # Set EFI_PROVIDER = "systemdboot" and MACHINE = "genericx86-64" in conf/local.conf
        features = 'EFI_PROVIDER = "systemd-boot"\n'
        features += 'MACHINE = "genericx86-64"'
        self.append_config(features)

    def _common_build(self):
        """
        Common build for test cases: 1445 , 1528
        """

        # Build a genericx86-64/efi systemdboot image
        bitbake('mtools-native core-image-minimal')


    @OETestID(1445)
    def test_efi_systemdboot_images_can_be_built(self):
        """
        Summary:     Check if systemd-boot images can be built correctly
        Expected:    1. File systemd-boot.efi should be available in $poky/build/tmp/deploy/images/genericx86-64
                     2. 'systemd-boot" can be built correctly
        Product:     oe-core
        Author:      Jose Perez Carranza <jose.perez.carranza@intel.com>
        AutomatedBy: Jose Perez Carranza <jose.perez.carranza@intel.com>
        """

        # We'd use DEPLOY_DIR_IMAGE here, except that we need its value for
        # MACHINE="genericx86-64 which is probably not the one configured
        systemdbootfile = os.path.join(get_bb_var('DEPLOY_DIR'), 'images', 'genericx86-64', 'systemd-bootx64.efi')

        self._common_setup()

        # Ensure we're actually testing that this gets built and not that
        # it was around from an earlier build
        bitbake('-c clean systemd-boot')
        runCmd('rm -f %s' % systemdbootfile)

        self._common_build()

        found = os.path.isfile(systemdbootfile)
        self.assertTrue(found, 'Systemd-Boot file %s not found' % systemdbootfile)

    @OETestID(1528)
    @OETestDepends(['systemd_boot.Systemdboot.test_efi_systemdboot_images_can_be_built'])
    def test_image_efi_file(self):

        """
        Summary:      Check if EFI bootloader for systemd is correctly build
        Dependencies: Image was built correctly on testcase 1445
        Steps:        1. Copy bootx64.efi file form the hddimg created
                      under build/tmp/deploy/images/genericx86-64
                      2. Check bootx64.efi was copied form hddimg
                      3. Verify the checksums from the copied and previously
                      created file are equal.
        Expected :    Systemd-bootx64.efi and bootx64.efi should be the same
                      hence checksums should be equal.
        Product:      oe-core
        Author:       Jose Perez Carranza <jose.perez.carranza at linux-intel.com>
        AutomatedBy:  Jose Perez Carranza <jose.perez.carranza at linux-intel.com>
        """

        systemdbootfile = os.path.join(get_bb_var('DEPLOY_DIR'), 'images', 'genericx86-64',
                                           'systemd-bootx64.efi')
        systemdbootimage = os.path.join(get_bb_var('DEPLOY_DIR'), 'images', 'genericx86-64',
                                                'core-image-minimal-genericx86-64.hddimg')
        imagebootfile = os.path.join(get_bb_var('DEPLOY_DIR'), 'images', 'genericx86-64',
                                                            'bootx64.efi')
        mcopynative = os.path.join(get_bb_var('STAGING_BINDIR_NATIVE'), 'mcopy')

        #Clean environment before start the test
        if os.path.isfile(imagebootfile):
            runCmd('rm -f %s' % imagebootfile)

            #Step 1
            runCmd('%s -i %s ::EFI/BOOT/bootx64.efi %s' % (mcopynative ,systemdbootimage,
                                                           imagebootfile))

            #Step 2
            found = os.path.isfile(imagebootfile)
            self.assertTrue(found, 'bootx64.efi file %s was not copied from image'
                            % imagebootfile)

            #Step 3
            result = runCmd('md5sum %s %s' % (systemdbootfile, imagebootfile))
            self.assertEqual(result.output.split()[0], result.output.split()[2],
                             '%s was not correclty generated' % imagebootfile)
