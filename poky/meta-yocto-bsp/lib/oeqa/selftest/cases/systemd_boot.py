import os

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars, runqemu

class Systemdboot(OESelftestTestCase):

    def test_efi_systemdboot_images_can_be_built(self):
        """
        Summary:     Check if systemd-boot images can be built correctly
        Expected:    1. File systemd-boot.efi should be available in $poky/build/tmp/deploy/images/genericx86-64
                     2. 'systemd-boot" can be built correctly
        Product:     oe-core
        Author:      Jose Perez Carranza <jose.perez.carranza@intel.com>
        AutomatedBy: Jose Perez Carranza <jose.perez.carranza@intel.com>
        """

        # Set EFI_PROVIDER = "systemdboot" and MACHINE = "genericx86-64" in conf/local.conf
        features = 'EFI_PROVIDER = "systemd-boot"\n'
        features += 'MACHINE = "genericx86-64"\n'
        features += 'COMPATIBLE_MACHINE:pn-ssh-pregen-hostkeys:genericx86-64 = "genericx86-64"\n'
        self.append_config(features)

        image = 'core-image-minimal'
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'], image)
        systemdbootfile = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], 'systemd-bootx64.efi')

        # Ensure we're actually testing that this gets built and not that
        # it was around from an earlier build
        bitbake('-c clean systemd-boot')
        runCmd('rm -f %s' % systemdbootfile)

        # Build a genericx86-64/efi systemdboot image
        bitbake('mtools-native core-image-minimal wic-tools')

        found = os.path.isfile(systemdbootfile)
        self.assertTrue(found, 'Systemd-Boot file %s not found' % systemdbootfile)

        """
        Summary:      Check if EFI bootloader for systemd is correctly build
        Dependencies: Image was built correctly on testcase 1445
        Steps:        1. Copy bootx64.efi file from the wic created
                      under build/tmp/deploy/images/genericx86-64
                      2. Check bootx64.efi was copied from wic
                      3. Verify the checksums from the copied and previously
                      created file are equal.
        Expected :    Systemd-bootx64.efi and bootx64.efi should be the same
                      hence checksums should be equal.
        Product:      oe-core
        Author:       Jose Perez Carranza <jose.perez.carranza at linux-intel.com>
        AutomatedBy:  Jose Perez Carranza <jose.perez.carranza at linux-intel.com>
        """

        systemdbootimage = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], '%s.wic' % bb_vars['IMAGE_LINK_NAME'])
        imagebootfile = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], 'bootx64.efi')

        # Clean environment before start the test
        if os.path.isfile(imagebootfile):
            runCmd('rm -f %s' % imagebootfile)

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        runCmd('wic cp %s:1/EFI/BOOT/bootx64.efi %s -n %s' % (systemdbootimage,
                                                           imagebootfile, sysroot))

        found = os.path.isfile(imagebootfile)
        self.assertTrue(found, 'bootx64.efi file %s was not copied from image'
                            % imagebootfile)

        result = runCmd('md5sum %s %s' % (systemdbootfile, imagebootfile))
        self.assertEqual(result.output.split()[0], result.output.split()[2],
                             '%s was not correclty generated' % imagebootfile)
