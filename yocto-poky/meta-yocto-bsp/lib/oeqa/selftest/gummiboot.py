from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, runqemu
from oeqa.utils.decorators import testcase
import re
import os
import sys
import logging


class Gummiboot(oeSelfTest):

    def _common_setup(self):
        """
        Common setup for test cases: 1101, 1103
        """

        # Set EFI_PROVIDER = "gummiboot" and MACHINE = "genericx86-64" in conf/local.conf
        features = 'EFI_PROVIDER = "gummiboot"\n'
        features += 'MACHINE = "genericx86-64"'
        self.append_config(features)

    def _common_build(self):
        """
        Common build for test cases: 1101, 1103
        """

        # Build a genericx86-64/efi gummiboot image
        bitbake('syslinux syslinux-native parted-native dosfstools-native mtools-native core-image-minimal')


    @testcase(1101)
    def test_efi_gummiboot_images_can_be_built(self):
        """
        Summary:     Check if efi/gummiboot images can be built
        Expected:    1. File gummibootx64.efi should be available in build/tmp/deploy/images/genericx86-64
                     2. Efi/gummiboot images can be built
        Product:     oe-core
        Author:      Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        # We'd use DEPLOY_DIR_IMAGE here, except that we need its value for
        # MACHINE="genericx86-64 which is probably not the one configured
        gummibootfile = os.path.join(get_bb_var('DEPLOY_DIR'), 'images', 'genericx86-64', 'gummibootx64.efi')

        self._common_setup()

        # Ensure we're actually testing that this gets built and not that
        # it was around from an earlier build
        bitbake('-c cleansstate gummiboot')
        runCmd('rm -f %s' % gummibootfile)

        self._common_build()

        found = os.path.isfile(gummibootfile)
        self.assertTrue(found, 'Gummiboot file %s not found' % gummibootfile)

    @testcase(1103)
    def test_wic_command_can_create_efi_gummiboot_installation_images(self):
        """
        Summary:     Check that wic command can create efi/gummiboot installation images
        Expected:    A .direct file in folder /var/tmp/wic/ must be created.
        Product:     oe-core
        Author:      Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        self._common_setup()
        self._common_build()

        # Create efi/gummiboot installation images
        wic_create_cmd = 'wic create mkgummidisk -e core-image-minimal'
        result = runCmd(wic_create_cmd)

        # Find file written by wic from output
        res = re.search('(/var/tmp/wic/.*\.direct)', result.output)
        if res:
            direct_file = res.group(1)
            # Check it actually exists
            if not os.path.exists(direct_file):
                self.fail('wic reported direct file "%s" does not exist; wic output:\n%s' % (direct_file, result.output))
        else:
            self.fail('No .direct file reported in wic output:\n%s' % result.output)
