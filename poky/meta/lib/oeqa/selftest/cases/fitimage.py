#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars
import os
import re

class FitImageTests(OESelftestTestCase):

    def _setup_uboot_tools_native(self):
        """build u-boot-tools-native and return RECIPE_SYSROOT_NATIVE"""
        bitbake("u-boot-tools-native -c addto_recipe_sysroot")
        return get_bb_var('RECIPE_SYSROOT_NATIVE', 'u-boot-tools-native')

    def _verify_fit_image_signature(self, uboot_tools_sysroot_native, fitimage_path, dtb_path, conf_name=None):
        """Verify the signature of a fit contfiguration

        The fit_check_sign utility from u-boot-tools-native is called.
        uboot-fit_check_sign -f fitImage -k $dtb_name -c conf-$dtb_name
        """
        fit_check_sign_path = os.path.join(uboot_tools_sysroot_native, 'usr', 'bin', 'uboot-fit_check_sign')
        cmd = '%s -f %s -k %s' % (fit_check_sign_path, fitimage_path, dtb_path)
        if conf_name:
            cmd += ' -c %s' % conf_name
        result = runCmd(cmd)
        self.logger.debug("%s\nreturned: %s\n%s", cmd, str(result.status), result.output)
        self.assertIn("Signature check OK", result.output)

    @staticmethod
    def _find_string_in_bin_file(file_path, search_string):
        """find stings in a binary file

        Shell equivalent: strings "$1" | grep "$2" | wc -l
        return number of matches
        """
        found_positions = 0
        with open(file_path, 'rb') as file:
            byte = file.read(1)
            current_position = 0
            current_match = 0
            while byte:
                char = byte.decode('ascii', errors='ignore')
                if char == search_string[current_match]:
                    current_match += 1
                    if current_match == len(search_string):
                        found_positions += 1
                        current_match = 0
                else:
                    current_match = 0
                current_position += 1
                byte = file.read(1)
        return found_positions


    def test_fit_image(self):
        """
        Summary:     Check if FIT image and Image Tree Source (its) are built
                     and the Image Tree Source has the correct fields.
        Expected:    1. fitImage and fitImage-its can be built
                     2. The type, load address, entrypoint address and
                     default values of kernel and ramdisk are as expected
                     in the Image Tree Source. Not all the fields are tested,
                     only the key fields that wont vary between different
                     architectures.
        Product:     oe-core
        Author:      Usama Arif <usama.arif@arm.com>
        """
        config = """
# Enable creation of fitImage
KERNEL_IMAGETYPE = "Image"
KERNEL_IMAGETYPES += " fitImage "
KERNEL_CLASSES = " kernel-fitimage "

# RAM disk variables including load address and entrypoint for kernel and RAM disk
IMAGE_FSTYPES += "cpio.gz"
INITRAMFS_IMAGE = "core-image-minimal"
# core-image-minimal is used as initramfs here, drop the rootfs suffix
IMAGE_NAME_SUFFIX:pn-core-image-minimal = ""
UBOOT_RD_LOADADDRESS = "0x88000000"
UBOOT_RD_ENTRYPOINT = "0x88000000"
UBOOT_LOADADDRESS = "0x80080000"
UBOOT_ENTRYPOINT = "0x80080000"
FIT_DESC = "A model description"
"""
        self.write_config(config)

        # fitImage is created as part of linux recipe
        image = "virtual/kernel"
        bitbake(image)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'INITRAMFS_IMAGE_NAME', 'KERNEL_FIT_LINK_NAME'], image)

        fitimage_its_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'],
            "fitImage-its-%s-%s" % (bb_vars['INITRAMFS_IMAGE_NAME'], bb_vars['KERNEL_FIT_LINK_NAME']))
        fitimage_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'],
            "fitImage-%s-%s" % (bb_vars['INITRAMFS_IMAGE_NAME'], bb_vars['KERNEL_FIT_LINK_NAME']))

        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertExists(fitimage_path, "%s FIT image doesn't exist" % (fitimage_path))

        # Check that the type, load address, entrypoint address and default
        # values for kernel and ramdisk in Image Tree Source are as expected.
        # The order of fields in the below array is important. Not all the
        # fields are tested, only the key fields that wont vary between
        # different architectures.
        its_field_check = [
            'description = "A model description";',
            'type = "kernel";',
            'load = <0x80080000>;',
            'entry = <0x80080000>;',
            'type = "ramdisk";',
            'load = <0x88000000>;',
            'entry = <0x88000000>;',
            'default = "conf-1";',
            'kernel = "kernel-1";',
            'ramdisk = "ramdisk-1";'
            ]

        with open(fitimage_its_path) as its_file:
            field_index = 0
            for line in its_file:
                if field_index == len(its_field_check):
                    break
                if its_field_check[field_index] in line:
                    field_index +=1

        if field_index != len(its_field_check): # if its equal, the test passed
            self.assertTrue(field_index == len(its_field_check),
                "Fields in Image Tree Source File %s did not match, error in finding %s"
                % (fitimage_its_path, its_field_check[field_index]))


    def test_sign_fit_image(self):
        """
        Summary:     Check if FIT image and Image Tree Source (its) are created
                     and signed correctly.
        Expected:    1) its and FIT image are built successfully
                     2) Scanning the its file indicates signing is enabled
                        as requested by UBOOT_SIGN_ENABLE (using keys generated
                        via FIT_GENERATE_KEYS)
                     3) Dumping the FIT image indicates signature values
                        are present (including for images as enabled via
                        FIT_SIGN_INDIVIDUAL)
                     4) Examination of the do_assemble_fitimage runfile/logfile
                        indicate that UBOOT_MKIMAGE, UBOOT_MKIMAGE_SIGN and
                        UBOOT_MKIMAGE_SIGN_ARGS are working as expected.
        Product:     oe-core
        Author:      Paul Eggleton <paul.eggleton@microsoft.com> based upon
                     work by Usama Arif <usama.arif@arm.com>
        """
        a_comment = "a smart comment"
        config = """
# Enable creation of fitImage
MACHINE = "beaglebone-yocto"
KERNEL_IMAGETYPES += " fitImage "
KERNEL_CLASSES = " kernel-fitimage "
UBOOT_SIGN_ENABLE = "1"
FIT_GENERATE_KEYS = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_IMG_KEYNAME = "img-oe-selftest"
UBOOT_SIGN_KEYNAME = "cfg-oe-selftest"
FIT_SIGN_INDIVIDUAL = "1"
UBOOT_MKIMAGE_SIGN_ARGS = "-c '%s'"
""" % a_comment

        self.write_config(config)

        # fitImage is created as part of linux recipe
        image = "virtual/kernel"
        bitbake(image)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'KERNEL_FIT_LINK_NAME'], image)

        fitimage_its_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'],
            "fitImage-its-%s" % (bb_vars['KERNEL_FIT_LINK_NAME']))
        fitimage_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'],
            "fitImage-%s.bin" % (bb_vars['KERNEL_FIT_LINK_NAME']))

        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertExists(fitimage_path, "%s FIT image doesn't exist" % (fitimage_path))

        req_itspaths = [
            ['/', 'images', 'kernel-1'],
            ['/', 'images', 'kernel-1', 'signature-1'],
            ['/', 'images', 'fdt-am335x-boneblack.dtb'],
            ['/', 'images', 'fdt-am335x-boneblack.dtb', 'signature-1'],
            ['/', 'configurations', 'conf-am335x-boneblack.dtb'],
            ['/', 'configurations', 'conf-am335x-boneblack.dtb', 'signature-1'],
        ]

        itspath = []
        itspaths = []
        linect = 0
        sigs = {}
        with open(fitimage_its_path) as its_file:
            linect += 1
            for line in its_file:
                line = line.strip()
                if line.endswith('};'):
                    itspath.pop()
                elif line.endswith('{'):
                    itspath.append(line[:-1].strip())
                    itspaths.append(itspath[:])
                elif itspath and itspath[-1] == 'signature-1':
                    itsdotpath = '.'.join(itspath)
                    if not itsdotpath in sigs:
                        sigs[itsdotpath] = {}
                    if not '=' in line or not line.endswith(';'):
                        self.fail('Unexpected formatting in %s sigs section line %d:%s' % (fitimage_its_path, linect, line))
                    key, value = line.split('=', 1)
                    sigs[itsdotpath][key.rstrip()] = value.lstrip().rstrip(';')

        for reqpath in req_itspaths:
            if not reqpath in itspaths:
                self.fail('Missing section in its file: %s' % reqpath)

        reqsigvalues_image = {
            'algo': '"sha256,rsa2048"',
            'key-name-hint': '"img-oe-selftest"',
        }
        reqsigvalues_config = {
            'algo': '"sha256,rsa2048"',
            'key-name-hint': '"cfg-oe-selftest"',
            'sign-images': '"kernel", "fdt"',
        }

        for itspath, values in sigs.items():
            if 'conf-' in itspath:
                reqsigvalues = reqsigvalues_config
            else:
                reqsigvalues = reqsigvalues_image
            for reqkey, reqvalue in reqsigvalues.items():
                value = values.get(reqkey, None)
                if value is None:
                    self.fail('Missing key "%s" in its file signature section %s' % (reqkey, itspath))
                self.assertEqual(value, reqvalue)

        # Dump the image to see if it really got signed
        uboot_tools_sysroot_native = self._setup_uboot_tools_native()
        dumpimage_path = os.path.join(uboot_tools_sysroot_native, 'usr', 'bin', 'dumpimage')
        result = runCmd('%s -l %s' % (dumpimage_path, fitimage_path))
        in_signed = None
        signed_sections = {}
        for line in result.output.splitlines():
            if line.startswith((' Configuration', ' Image')):
                in_signed = re.search(r'\((.*)\)', line).groups()[0]
            elif re.match('^ *', line) in (' ', ''):
                in_signed = None
            elif in_signed:
                if not in_signed in signed_sections:
                    signed_sections[in_signed] = {}
                key, value = line.split(':', 1)
                signed_sections[in_signed][key.strip()] = value.strip()
        self.assertIn('kernel-1', signed_sections)
        self.assertIn('fdt-am335x-boneblack.dtb', signed_sections)
        self.assertIn('conf-am335x-boneblack.dtb', signed_sections)
        for signed_section, values in signed_sections.items():
            value = values.get('Sign algo', None)
            if signed_section.startswith("conf"):
                self.assertEqual(value, 'sha256,rsa2048:cfg-oe-selftest', 'Signature algorithm for %s not expected value' % signed_section)
            else:
                self.assertEqual(value, 'sha256,rsa2048:img-oe-selftest', 'Signature algorithm for %s not expected value' % signed_section)
            value = values.get('Sign value', None)
            self.assertEqual(len(value), 512, 'Signature value for section %s not expected length' % signed_section)

        # Search for the string passed to mkimage: 1 kernel + 3 DTBs + config per DTB = 7 sections
        # Looks like mkimage supports to add a comment but does not support to read it back.
        found_comments = FitImageTests._find_string_in_bin_file(fitimage_path, a_comment)
        self.assertEqual(found_comments, 7, "Expected 7 signed and commented section in the fitImage.")

        # Verify the signature for all configurations = DTBs
        for dtb in ['am335x-bone.dtb', 'am335x-boneblack.dtb', 'am335x-bonegreen.dtb']:
            self._verify_fit_image_signature(uboot_tools_sysroot_native, fitimage_path,
                                             os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], dtb), 'conf-' + dtb)

    def test_uboot_fit_image(self):
        """
        Summary:     Check if Uboot FIT image and Image Tree Source
                     (its) are built and the Image Tree Source has the
                     correct fields.
        Expected:    1. u-boot-fitImage and u-boot-its can be built
                     2. The type, load address, entrypoint address and
                     default values of U-boot image are correct in the
                     Image Tree Source. Not all the fields are tested,
                     only the key fields that wont vary between
                     different architectures.
        Product:     oe-core
        Author:      Klaus Heinrich Kiwi <klaus@linux.vnet.ibm.com>
                     based on work by Usama Arif <usama.arif@arm.com>
        """
        config = """
# We need at least CONFIG_SPL_LOAD_FIT and CONFIG_SPL_OF_CONTROL set
MACHINE = "qemuarm"
UBOOT_MACHINE = "am57xx_evm_defconfig"
SPL_BINARY = "MLO"

# Enable creation of the U-Boot fitImage
UBOOT_FITIMAGE_ENABLE = "1"

# (U-boot) fitImage properties
UBOOT_LOADADDRESS = "0x80080000"
UBOOT_ENTRYPOINT = "0x80080000"
UBOOT_FIT_DESC = "A model description"

# Enable creation of Kernel fitImage
KERNEL_IMAGETYPES += " fitImage "
KERNEL_CLASSES = " kernel-fitimage"
UBOOT_SIGN_ENABLE = "1"
FIT_GENERATE_KEYS = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_IMG_KEYNAME = "img-oe-selftest"
UBOOT_SIGN_KEYNAME = "cfg-oe-selftest"
FIT_SIGN_INDIVIDUAL = "1"
"""
        self.write_config(config)

        # The U-Boot fitImage is created as part of the U-Boot recipe
        bitbake("virtual/bootloader")

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        machine = get_bb_var('MACHINE')
        fitimage_its_path = os.path.join(deploy_dir_image,
            "u-boot-its-%s" % (machine,))
        fitimage_path = os.path.join(deploy_dir_image,
            "u-boot-fitImage-%s" % (machine,))

        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertExists(fitimage_path, "%s FIT image doesn't exist" % (fitimage_path))

        # Check that the type, load address, entrypoint address and default
        # values for kernel and ramdisk in Image Tree Source are as expected.
        # The order of fields in the below array is important. Not all the
        # fields are tested, only the key fields that wont vary between
        # different architectures.
        its_field_check = [
            'description = "A model description";',
            'type = "standalone";',
            'load = <0x80080000>;',
            'entry = <0x80080000>;',
            'default = "conf";',
            'loadables = "uboot";',
            'fdt = "fdt";'
            ]

        with open(fitimage_its_path) as its_file:
            field_index = 0
            for line in its_file:
                if field_index == len(its_field_check):
                    break
                if its_field_check[field_index] in line:
                    field_index +=1

        if field_index != len(its_field_check): # if its equal, the test passed
            self.assertTrue(field_index == len(its_field_check),
                "Fields in Image Tree Source File %s did not match, error in finding %s"
                % (fitimage_its_path, its_field_check[field_index]))

    def test_uboot_sign_fit_image(self):
        """
        Summary:     Check if Uboot FIT image and Image Tree Source
                     (its) are built and the Image Tree Source has the
                     correct fields, in the scenario where the Kernel
                     is also creating/signing it's fitImage.
        Expected:    1. u-boot-fitImage and u-boot-its can be built
                     2. The type, load address, entrypoint address and
                     default values of U-boot image are correct in the
                     Image Tree Source. Not all the fields are tested,
                     only the key fields that wont vary between
                     different architectures.
        Product:     oe-core
        Author:      Klaus Heinrich Kiwi <klaus@linux.vnet.ibm.com>
                     based on work by Usama Arif <usama.arif@arm.com>
        """
        config = """
# We need at least CONFIG_SPL_LOAD_FIT and CONFIG_SPL_OF_CONTROL set
MACHINE = "qemuarm"
UBOOT_MACHINE = "am57xx_evm_defconfig"
SPL_BINARY = "MLO"

# Enable creation of the U-Boot fitImage
UBOOT_FITIMAGE_ENABLE = "1"

# (U-boot) fitImage properties
UBOOT_LOADADDRESS = "0x80080000"
UBOOT_ENTRYPOINT = "0x80080000"
UBOOT_FIT_DESC = "A model description"
KERNEL_IMAGETYPES += " fitImage "
KERNEL_CLASSES = " kernel-fitimage "
UBOOT_SIGN_ENABLE = "1"
FIT_GENERATE_KEYS = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_IMG_KEYNAME = "img-oe-selftest"
UBOOT_SIGN_KEYNAME = "cfg-oe-selftest"
FIT_SIGN_INDIVIDUAL = "1"
UBOOT_MKIMAGE_SIGN_ARGS = "-c 'a smart U-Boot comment'"
"""
        self.write_config(config)

        # The U-Boot fitImage is created as part of the U-Boot recipe
        bitbake("virtual/bootloader")

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        machine = get_bb_var('MACHINE')
        fitimage_its_path = os.path.join(deploy_dir_image,
            "u-boot-its-%s" % (machine,))
        fitimage_path = os.path.join(deploy_dir_image,
            "u-boot-fitImage-%s" % (machine,))

        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertExists(fitimage_path, "%s FIT image doesn't exist" % (fitimage_path))

        # Check that the type, load address, entrypoint address and default
        # values for kernel and ramdisk in Image Tree Source are as expected.
        # The order of fields in the below array is important. Not all the
        # fields are tested, only the key fields that wont vary between
        # different architectures.
        its_field_check = [
            'description = "A model description";',
            'type = "standalone";',
            'load = <0x80080000>;',
            'entry = <0x80080000>;',
            'default = "conf";',
            'loadables = "uboot";',
            'fdt = "fdt";'
            ]

        with open(fitimage_its_path) as its_file:
            field_index = 0
            for line in its_file:
                if field_index == len(its_field_check):
                    break
                if its_field_check[field_index] in line:
                    field_index +=1

        if field_index != len(its_field_check): # if its equal, the test passed
            self.assertTrue(field_index == len(its_field_check),
                "Fields in Image Tree Source File %s did not match, error in finding %s"
                % (fitimage_its_path, its_field_check[field_index]))


    def test_sign_standalone_uboot_fit_image(self):
        """
        Summary:     Check if U-Boot FIT image and Image Tree Source (its) are
                     created and signed correctly for the scenario where only
                     the U-Boot proper fitImage is being created and signed.
        Expected:    1) U-Boot its and FIT image are built successfully
                     2) Scanning the its file indicates signing is enabled
                        as requested by SPL_SIGN_ENABLE (using keys generated
                        via UBOOT_FIT_GENERATE_KEYS)
                     3) Dumping the FIT image indicates signature values
                        are present
                     4) Examination of the do_uboot_assemble_fitimage
                     runfile/logfile indicate that UBOOT_MKIMAGE, UBOOT_MKIMAGE_SIGN
                     and SPL_MKIMAGE_SIGN_ARGS are working as expected.
        Product:     oe-core
        Author:      Klaus Heinrich Kiwi <klaus@linux.vnet.ibm.com> based upon
                     work by Paul Eggleton <paul.eggleton@microsoft.com> and
                     Usama Arif <usama.arif@arm.com>
        """
        a_comment = "a smart U-Boot comment"
        config = """
# There's no U-boot deconfig with CONFIG_FIT_SIGNATURE yet, so we need at
# least CONFIG_SPL_LOAD_FIT and CONFIG_SPL_OF_CONTROL set
MACHINE = "qemuarm"
UBOOT_MACHINE = "am57xx_evm_defconfig"
SPL_BINARY = "MLO"
# The kernel-fitimage class is a dependency even if we're only
# creating/signing the U-Boot fitImage
KERNEL_CLASSES = " kernel-fitimage"
# Enable creation and signing of the U-Boot fitImage
UBOOT_FITIMAGE_ENABLE = "1"
SPL_SIGN_ENABLE = "1"
SPL_SIGN_KEYNAME = "spl-oe-selftest"
SPL_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_DTB_BINARY = "u-boot.dtb"
UBOOT_ENTRYPOINT  = "0x80000000"
UBOOT_LOADADDRESS = "0x80000000"
UBOOT_DTB_LOADADDRESS = "0x82000000"
UBOOT_ARCH = "arm"
SPL_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
SPL_MKIMAGE_SIGN_ARGS = "-c '%s'"
UBOOT_EXTLINUX = "0"
UBOOT_FIT_GENERATE_KEYS = "1"
UBOOT_FIT_HASH_ALG = "sha256"
""" % a_comment

        self.write_config(config)

        # The U-Boot fitImage is created as part of the U-Boot recipe
        bitbake("virtual/bootloader")

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        machine = get_bb_var('MACHINE')
        fitimage_its_path = os.path.join(deploy_dir_image,
            "u-boot-its-%s" % (machine,))
        fitimage_path = os.path.join(deploy_dir_image,
            "u-boot-fitImage-%s" % (machine,))

        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertExists(fitimage_path, "%s FIT image doesn't exist" % (fitimage_path))

        req_itspaths = [
            ['/', 'images', 'uboot'],
            ['/', 'images', 'uboot', 'signature'],
            ['/', 'images', 'fdt'],
            ['/', 'images', 'fdt', 'signature'],
        ]

        itspath = []
        itspaths = []
        linect = 0
        sigs = {}
        with open(fitimage_its_path) as its_file:
            linect += 1
            for line in its_file:
                line = line.strip()
                if line.endswith('};'):
                    itspath.pop()
                elif line.endswith('{'):
                    itspath.append(line[:-1].strip())
                    itspaths.append(itspath[:])
                elif itspath and itspath[-1] == 'signature':
                    itsdotpath = '.'.join(itspath)
                    if not itsdotpath in sigs:
                        sigs[itsdotpath] = {}
                    if not '=' in line or not line.endswith(';'):
                        self.fail('Unexpected formatting in %s sigs section line %d:%s' % (fitimage_its_path, linect, line))
                    key, value = line.split('=', 1)
                    sigs[itsdotpath][key.rstrip()] = value.lstrip().rstrip(';')

        for reqpath in req_itspaths:
            if not reqpath in itspaths:
                self.fail('Missing section in its file: %s' % reqpath)

        reqsigvalues_image = {
            'algo': '"sha256,rsa2048"',
            'key-name-hint': '"spl-oe-selftest"',
        }

        for itspath, values in sigs.items():
            reqsigvalues = reqsigvalues_image
            for reqkey, reqvalue in reqsigvalues.items():
                value = values.get(reqkey, None)
                if value is None:
                    self.fail('Missing key "%s" in its file signature section %s' % (reqkey, itspath))
                self.assertEqual(value, reqvalue)

        # Dump the image to see if it really got signed
        uboot_tools_sysroot_native = self._setup_uboot_tools_native()
        dumpimage_path = os.path.join(uboot_tools_sysroot_native, 'usr', 'bin', 'dumpimage')
        result = runCmd('%s -l %s' % (dumpimage_path, fitimage_path))
        in_signed = None
        signed_sections = {}
        for line in result.output.splitlines():
            if line.startswith((' Image')):
                in_signed = re.search(r'\((.*)\)', line).groups()[0]
            elif re.match(' \w', line):
                in_signed = None
            elif in_signed:
                if not in_signed in signed_sections:
                    signed_sections[in_signed] = {}
                key, value = line.split(':', 1)
                signed_sections[in_signed][key.strip()] = value.strip()
        self.assertIn('uboot', signed_sections)
        self.assertIn('fdt', signed_sections)
        for signed_section, values in signed_sections.items():
            value = values.get('Sign algo', None)
            self.assertEqual(value, 'sha256,rsa2048:spl-oe-selftest', 'Signature algorithm for %s not expected value' % signed_section)
            value = values.get('Sign value', None)
            self.assertEqual(len(value), 512, 'Signature value for section %s not expected length' % signed_section)

        # Check for SPL_MKIMAGE_SIGN_ARGS
        # Looks like mkimage supports to add a comment but does not support to read it back.
        found_comments = FitImageTests._find_string_in_bin_file(fitimage_path, a_comment)
        self.assertEqual(found_comments, 2, "Expected 2 signed and commented section in the fitImage.")

        # Verify the signature
        self._verify_fit_image_signature(uboot_tools_sysroot_native, fitimage_path,
                                         os.path.join(deploy_dir_image, 'u-boot-spl.dtb'))


    def test_sign_cascaded_uboot_fit_image(self):
        """
        Summary:     Check if U-Boot FIT image and Image Tree Source (its) are
                     created and signed correctly for the scenario where both
                     U-Boot proper and Kernel fitImages are being created and
                     signed.
        Expected:    1) U-Boot its and FIT image are built successfully
                     2) Scanning the its file indicates signing is enabled
                        as requested by SPL_SIGN_ENABLE (using keys generated
                        via UBOOT_FIT_GENERATE_KEYS)
                     3) Dumping the FIT image indicates signature values
                        are present
                     4) Examination of the do_uboot_assemble_fitimage
                     runfile/logfile indicate that UBOOT_MKIMAGE, UBOOT_MKIMAGE_SIGN
                     and SPL_MKIMAGE_SIGN_ARGS are working as expected.
        Product:     oe-core
        Author:      Klaus Heinrich Kiwi <klaus@linux.vnet.ibm.com> based upon
                     work by Paul Eggleton <paul.eggleton@microsoft.com> and
                     Usama Arif <usama.arif@arm.com>
        """
        a_comment = "a smart cascaded U-Boot comment"
        config = """
# There's no U-boot deconfig with CONFIG_FIT_SIGNATURE yet, so we need at
# least CONFIG_SPL_LOAD_FIT and CONFIG_SPL_OF_CONTROL set
MACHINE = "qemuarm"
UBOOT_MACHINE = "am57xx_evm_defconfig"
SPL_BINARY = "MLO"
# Enable creation and signing of the U-Boot fitImage
UBOOT_FITIMAGE_ENABLE = "1"
SPL_SIGN_ENABLE = "1"
SPL_SIGN_KEYNAME = "spl-cascaded-oe-selftest"
SPL_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_DTB_BINARY = "u-boot.dtb"
UBOOT_ENTRYPOINT  = "0x80000000"
UBOOT_LOADADDRESS = "0x80000000"
UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
UBOOT_MKIMAGE_SIGN_ARGS = "-c '%s'"
UBOOT_DTB_LOADADDRESS = "0x82000000"
UBOOT_ARCH = "arm"
SPL_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
SPL_MKIMAGE_SIGN_ARGS = "-c 'a smart cascaded U-Boot comment'"
UBOOT_EXTLINUX = "0"
UBOOT_FIT_GENERATE_KEYS = "1"
UBOOT_FIT_HASH_ALG = "sha256"
KERNEL_IMAGETYPES += " fitImage "
KERNEL_CLASSES = " kernel-fitimage "
UBOOT_SIGN_ENABLE = "1"
FIT_GENERATE_KEYS = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_IMG_KEYNAME = "img-oe-selftest"
UBOOT_SIGN_KEYNAME = "cfg-oe-selftest"
FIT_SIGN_INDIVIDUAL = "1"
""" % a_comment
        self.write_config(config)

        # The U-Boot fitImage is created as part of the U-Boot recipe
        bitbake("virtual/bootloader")

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        machine = get_bb_var('MACHINE')
        fitimage_its_path = os.path.join(deploy_dir_image,
            "u-boot-its-%s" % (machine,))
        fitimage_path = os.path.join(deploy_dir_image,
            "u-boot-fitImage-%s" % (machine,))

        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertExists(fitimage_path, "%s FIT image doesn't exist" % (fitimage_path))

        req_itspaths = [
            ['/', 'images', 'uboot'],
            ['/', 'images', 'uboot', 'signature'],
            ['/', 'images', 'fdt'],
            ['/', 'images', 'fdt', 'signature'],
        ]

        itspath = []
        itspaths = []
        linect = 0
        sigs = {}
        with open(fitimage_its_path) as its_file:
            linect += 1
            for line in its_file:
                line = line.strip()
                if line.endswith('};'):
                    itspath.pop()
                elif line.endswith('{'):
                    itspath.append(line[:-1].strip())
                    itspaths.append(itspath[:])
                elif itspath and itspath[-1] == 'signature':
                    itsdotpath = '.'.join(itspath)
                    if not itsdotpath in sigs:
                        sigs[itsdotpath] = {}
                    if not '=' in line or not line.endswith(';'):
                        self.fail('Unexpected formatting in %s sigs section line %d:%s' % (fitimage_its_path, linect, line))
                    key, value = line.split('=', 1)
                    sigs[itsdotpath][key.rstrip()] = value.lstrip().rstrip(';')

        for reqpath in req_itspaths:
            if not reqpath in itspaths:
                self.fail('Missing section in its file: %s' % reqpath)

        reqsigvalues_image = {
            'algo': '"sha256,rsa2048"',
            'key-name-hint': '"spl-cascaded-oe-selftest"',
        }

        for itspath, values in sigs.items():
            reqsigvalues = reqsigvalues_image
            for reqkey, reqvalue in reqsigvalues.items():
                value = values.get(reqkey, None)
                if value is None:
                    self.fail('Missing key "%s" in its file signature section %s' % (reqkey, itspath))
                self.assertEqual(value, reqvalue)

        # Dump the image to see if it really got signed
        uboot_tools_sysroot_native = self._setup_uboot_tools_native()
        dumpimage_path = os.path.join(uboot_tools_sysroot_native, 'usr', 'bin', 'dumpimage')
        result = runCmd('%s -l %s' % (dumpimage_path, fitimage_path))
        in_signed = None
        signed_sections = {}
        for line in result.output.splitlines():
            if line.startswith((' Image')):
                in_signed = re.search(r'\((.*)\)', line).groups()[0]
            elif re.match(' \w', line):
                in_signed = None
            elif in_signed:
                if not in_signed in signed_sections:
                    signed_sections[in_signed] = {}
                key, value = line.split(':', 1)
                signed_sections[in_signed][key.strip()] = value.strip()
        self.assertIn('uboot', signed_sections)
        self.assertIn('fdt', signed_sections)
        for signed_section, values in signed_sections.items():
            value = values.get('Sign algo', None)
            self.assertEqual(value, 'sha256,rsa2048:spl-cascaded-oe-selftest', 'Signature algorithm for %s not expected value' % signed_section)
            value = values.get('Sign value', None)
            self.assertEqual(len(value), 512, 'Signature value for section %s not expected length' % signed_section)

        # Check for SPL_MKIMAGE_SIGN_ARGS
        # Looks like mkimage supports to add a comment but does not support to read it back.
        found_comments = FitImageTests._find_string_in_bin_file(fitimage_path, a_comment)
        self.assertEqual(found_comments, 2, "Expected 2 signed and commented section in the fitImage.")

        # Verify the signature
        self._verify_fit_image_signature(uboot_tools_sysroot_native, fitimage_path,
                                         os.path.join(deploy_dir_image, 'u-boot-spl.dtb'))


    def test_initramfs_bundle(self):
        """
        Summary:     Verifies the content of the initramfs bundle node in the FIT Image Tree Source (its)
                     The FIT settings are set by the test case.
                     The machine used is beaglebone-yocto.
        Expected:    1. The ITS is generated with initramfs bundle support
                     2. All the fields in the kernel node are as expected (matching the
                        conf settings)
                     3. The kernel is included in all the available configurations and
                        its hash is included in the configuration signature

        Product:     oe-core
        Author:      Abdellatif El Khlifi <abdellatif.elkhlifi@arm.com>
        """

        config = """
DISTRO="poky"
MACHINE = "beaglebone-yocto"
INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "core-image-minimal-initramfs"
INITRAMFS_SCRIPTS = ""
UBOOT_MACHINE = "am335x_evm_defconfig"
KERNEL_CLASSES = " kernel-fitimage "
KERNEL_IMAGETYPES = "fitImage"
UBOOT_SIGN_ENABLE = "1"
UBOOT_SIGN_KEYNAME = "beaglebonekey"
UBOOT_SIGN_KEYDIR ?= "${DEPLOY_DIR_IMAGE}"
UBOOT_DTB_BINARY = "u-boot.dtb"
UBOOT_ENTRYPOINT  = "0x80000000"
UBOOT_LOADADDRESS = "0x80000000"
UBOOT_DTB_LOADADDRESS = "0x82000000"
UBOOT_ARCH = "arm"
UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
UBOOT_MKIMAGE_KERNEL_TYPE = "kernel"
UBOOT_EXTLINUX = "0"
FIT_GENERATE_KEYS = "1"
KERNEL_IMAGETYPE_REPLACEMENT = "zImage"
FIT_KERNEL_COMP_ALG = "none"
FIT_HASH_ALG = "sha256"
"""
        self.write_config(config)

        # fitImage is created as part of linux recipe
        bitbake("virtual/kernel")

        bb_vars = get_bb_vars([
            'DEPLOY_DIR_IMAGE',
            'FIT_HASH_ALG',
            'FIT_KERNEL_COMP_ALG',
            'INITRAMFS_IMAGE',
            'MACHINE',
            'UBOOT_ARCH',
            'UBOOT_ENTRYPOINT',
            'UBOOT_LOADADDRESS',
            'UBOOT_MKIMAGE_KERNEL_TYPE'
            ],
            'virtual/kernel')
        fitimage_its_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'],
                    "fitImage-its-%s-%s-%s" % (bb_vars['INITRAMFS_IMAGE'], bb_vars['MACHINE'], bb_vars['MACHINE']))
        fitimage_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'],"fitImage")

        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertExists(fitimage_path, "%s FIT image doesn't exist" % (fitimage_path))

        its_file = open(fitimage_its_path)

        its_lines = [line.strip() for line in its_file.readlines()]

        exp_node_lines = [
            'kernel-1 {',
            'description = "Linux kernel";',
            'data = /incbin/("linux.bin");',
            'type = "' + str(bb_vars['UBOOT_MKIMAGE_KERNEL_TYPE']) + '";',
            'arch = "' + str(bb_vars['UBOOT_ARCH']) + '";',
            'os = "linux";',
            'compression = "' + str(bb_vars['FIT_KERNEL_COMP_ALG']) + '";',
            'load = <' + str(bb_vars['UBOOT_LOADADDRESS']) + '>;',
            'entry = <' + str(bb_vars['UBOOT_ENTRYPOINT']) + '>;',
            'hash-1 {',
            'algo = "' + str(bb_vars['FIT_HASH_ALG']) +'";',
            '};',
            '};'
        ]

        node_str = exp_node_lines[0]

        print ("checking kernel node\n")
        self.assertIn(node_str, its_lines)

        node_start_idx = its_lines.index(node_str)
        node = its_lines[node_start_idx:(node_start_idx + len(exp_node_lines))]

        # Remove the absolute path. This refers to WORKDIR which is not always predictable.
        re_data = re.compile(r'^data = /incbin/\(.*/linux\.bin"\);$')
        node = [re.sub(re_data, 'data = /incbin/("linux.bin");', cfg_str) for cfg_str in node]

        self.assertEqual(node, exp_node_lines, "kernel node does not match expectation")

        rx_configs = re.compile("^conf-.*")
        its_configs = list(filter(rx_configs.match, its_lines))

        for cfg_str in its_configs:
            cfg_start_idx = its_lines.index(cfg_str)
            line_idx = cfg_start_idx + 2
            node_end = False
            while node_end == False:
                if its_lines[line_idx] == "};" and its_lines[line_idx-1] == "};" :
                    node_end = True
                line_idx = line_idx + 1

            node = its_lines[cfg_start_idx:line_idx]
            print("checking configuration " + cfg_str.rstrip(" {"))
            rx_desc_line = re.compile(r'^description = ".*Linux kernel.*')
            self.assertEqual(len(list(filter(rx_desc_line.match, node))), 1, "kernel keyword not found in the description line")

            self.assertIn('kernel = "kernel-1";', node)

            rx_sign_line = re.compile(r'^sign-images = .*kernel.*')
            self.assertEqual(len(list(filter(rx_sign_line.match, node))), 1, "kernel hash not signed")

        # Verify the signature
        uboot_tools_sysroot_native = self._setup_uboot_tools_native()
        self._verify_fit_image_signature(uboot_tools_sysroot_native, fitimage_path, os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], 'am335x-bone.dtb'))
