from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, runqemu
from oeqa.utils.decorators import testcase
import re
import os
import sys
import logging


class Systemdboot(oeSelfTest):

    def _common_setup(self):
        """
        Common setup for test cases: 1445, XXXX
        """

        # Set EFI_PROVIDER = "gummiboot" and MACHINE = "genericx86-64" in conf/local.conf
        features = 'EFI_PROVIDER = "systemd-boot"\n'
        features += 'MACHINE = "genericx86-64"'
        self.append_config(features)

    def _common_build(self):
        """
        Common build for test cases: 1445 , XXXX
        """

        # Build a genericx86-64/efi gummiboot image
        bitbake('mtools-native core-image-minimal')


    @testcase(1445)
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
        bitbake('-c cleansstate systemd-boot')
        runCmd('rm -f %s' % systemdbootfile)

        self._common_build()

        found = os.path.isfile(systemdbootfile)
        self.assertTrue(found, 'Systemd-Boot file %s not found' % systemdbootfile)
