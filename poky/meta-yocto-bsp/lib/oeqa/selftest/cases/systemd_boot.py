import os

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.depends import OETestDepends
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, runqemu

class Systemdboot(OESelftestTestCase):

    @OETestID(1445)
    @OETestID(1528)
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
        features += 'MACHINE = "genericx86-64"'
        self.append_config(features)

        deploydir = get_bb_var('DEPLOY_DIR_IMAGE', "core-image-minimal")
        systemdbootfile = os.path.join(deploydir, 'systemd-bootx64.efi')

        # Ensure we're actually testing that this gets built and not that
        # it was around from an earlier build
        bitbake('-c clean systemd-boot')
        runCmd('rm -f %s' % systemdbootfile)

        # Build a genericx86-64/efi systemdboot image
        bitbake('mtools-native core-image-minimal')

        found = os.path.isfile(systemdbootfile)
        self.assertTrue(found, 'Systemd-Boot file %s not found' % systemdbootfile)

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

        systemdbootimage = os.path.join(deploydir, 'core-image-minimal-genericx86-64.hddimg')
        imagebootfile = os.path.join(deploydir, 'bootx64.efi')
        mcopynative = os.path.join(get_bb_var('STAGING_BINDIR_NATIVE', "core-image-minimal"), 'mcopy')

        # Clean environment before start the test
        if os.path.isfile(imagebootfile):
            runCmd('rm -f %s' % imagebootfile)

        runCmd('%s -i %s ::EFI/BOOT/bootx64.efi %s' % (mcopynative ,systemdbootimage,
                                                           imagebootfile))

        found = os.path.isfile(imagebootfile)
        self.assertTrue(found, 'bootx64.efi file %s was not copied from image'
                            % imagebootfile)

        result = runCmd('md5sum %s %s' % (systemdbootfile, imagebootfile))
        self.assertEqual(result.output.split()[0], result.output.split()[2],
                             '%s was not correclty generated' % imagebootfile)
