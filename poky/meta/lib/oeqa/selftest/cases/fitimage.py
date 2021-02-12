#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, runqemu
import os
import json
import re

class FitImageTests(OESelftestTestCase):

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
UBOOT_RD_LOADADDRESS = "0x88000000"
UBOOT_RD_ENTRYPOINT = "0x88000000"
UBOOT_LOADADDRESS = "0x80080000"
UBOOT_ENTRYPOINT = "0x80080000"
FIT_DESC = "A model description"
"""
        self.write_config(config)

        # fitImage is created as part of linux recipe
        bitbake("virtual/kernel")

        image_type = "core-image-minimal"
        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        machine = get_bb_var('MACHINE')
        fitimage_its_path = os.path.join(deploy_dir_image,
            "fitImage-its-%s-%s-%s" % (image_type, machine, machine))
        fitimage_path = os.path.join(deploy_dir_image,
            "fitImage-%s-%s-%s" % (image_type, machine, machine))

        self.assertTrue(os.path.exists(fitimage_its_path),
            "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertTrue(os.path.exists(fitimage_path),
            "%s FIT image doesn't exist" % (fitimage_path))

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
            'default = "conf@1";',
            'kernel = "kernel@1";',
            'ramdisk = "ramdisk@1";'
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
        config = """
# Enable creation of fitImage
MACHINE = "beaglebone-yocto"
KERNEL_IMAGETYPES += " fitImage "
KERNEL_CLASSES = " kernel-fitimage test-mkimage-wrapper "
UBOOT_SIGN_ENABLE = "1"
FIT_GENERATE_KEYS = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_KEYNAME = "oe-selftest"
FIT_SIGN_INDIVIDUAL = "1"
UBOOT_MKIMAGE_SIGN_ARGS = "-c 'a smart comment'"
"""
        self.write_config(config)

        # fitImage is created as part of linux recipe
        bitbake("virtual/kernel")

        image_type = "core-image-minimal"
        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        machine = get_bb_var('MACHINE')
        fitimage_its_path = os.path.join(deploy_dir_image,
            "fitImage-its-%s" % (machine,))
        fitimage_path = os.path.join(deploy_dir_image,
            "fitImage-%s.bin" % (machine,))

        self.assertTrue(os.path.exists(fitimage_its_path),
            "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertTrue(os.path.exists(fitimage_path),
            "%s FIT image doesn't exist" % (fitimage_path))

        req_itspaths = [
            ['/', 'images', 'kernel@1'],
            ['/', 'images', 'kernel@1', 'signature@1'],
            ['/', 'images', 'fdt@am335x-boneblack.dtb'],
            ['/', 'images', 'fdt@am335x-boneblack.dtb', 'signature@1'],
            ['/', 'configurations', 'conf@am335x-boneblack.dtb'],
            ['/', 'configurations', 'conf@am335x-boneblack.dtb', 'signature@1'],
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
                elif itspath and itspath[-1] == 'signature@1':
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
            'key-name-hint': '"oe-selftest"',
        }
        reqsigvalues_config = {
            'algo': '"sha256,rsa2048"',
            'key-name-hint': '"oe-selftest"',
            'sign-images': '"kernel", "fdt"',
        }

        for itspath, values in sigs.items():
            if 'conf@' in itspath:
                reqsigvalues = reqsigvalues_config
            else:
                reqsigvalues = reqsigvalues_image
            for reqkey, reqvalue in reqsigvalues.items():
                value = values.get(reqkey, None)
                if value is None:
                    self.fail('Missing key "%s" in its file signature section %s' % (reqkey, itspath))
                self.assertEqual(value, reqvalue)

        # Dump the image to see if it really got signed
        bitbake("u-boot-tools-native -c addto_recipe_sysroot")
        result = runCmd('bitbake -e u-boot-tools-native | grep ^RECIPE_SYSROOT_NATIVE=')
        recipe_sysroot_native = result.output.split('=')[1].strip('"')
        dumpimage_path = os.path.join(recipe_sysroot_native, 'usr', 'bin', 'dumpimage')
        result = runCmd('%s -l %s' % (dumpimage_path, fitimage_path))
        in_signed = None
        signed_sections = {}
        for line in result.output.splitlines():
            if line.startswith((' Configuration', ' Image')):
                in_signed = re.search('\((.*)\)', line).groups()[0]
            elif re.match('^ *', line) in (' ', ''):
                in_signed = None
            elif in_signed:
                if not in_signed in signed_sections:
                    signed_sections[in_signed] = {}
                key, value = line.split(':', 1)
                signed_sections[in_signed][key.strip()] = value.strip()
        self.assertIn('kernel@1', signed_sections)
        self.assertIn('fdt@am335x-boneblack.dtb', signed_sections)
        self.assertIn('conf@am335x-boneblack.dtb', signed_sections)
        for signed_section, values in signed_sections.items():
            value = values.get('Sign algo', None)
            self.assertEqual(value, 'sha256,rsa2048:oe-selftest', 'Signature algorithm for %s not expected value' % signed_section)
            value = values.get('Sign value', None)
            self.assertEqual(len(value), 512, 'Signature value for section %s not expected length' % signed_section)

        # Check for UBOOT_MKIMAGE_SIGN_ARGS
        result = runCmd('bitbake -e virtual/kernel | grep ^T=')
        tempdir = result.output.split('=', 1)[1].strip().strip('')
        result = runCmd('grep "a smart comment" %s/run.do_assemble_fitimage' % tempdir, ignore_status=True)
        self.assertEqual(result.status, 0, 'UBOOT_MKIMAGE_SIGN_ARGS value did not get used')

        # Check for evidence of test-mkimage-wrapper class
        result = runCmd('grep "### uboot-mkimage wrapper message" %s/log.do_assemble_fitimage' % tempdir, ignore_status=True)
        self.assertEqual(result.status, 0, 'UBOOT_MKIMAGE did not work')
        result = runCmd('grep "### uboot-mkimage signing wrapper message" %s/log.do_assemble_fitimage' % tempdir, ignore_status=True)
        self.assertEqual(result.status, 0, 'UBOOT_MKIMAGE_SIGN did not work')

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
UBOOT_EXTLINUX = "0"
FIT_GENERATE_KEYS = "1"
KERNEL_IMAGETYPE_REPLACEMENT = "zImage"
FIT_HASH_ALG = "sha256"
"""
        self.write_config(config)

        # fitImage is created as part of linux recipe
        bitbake("virtual/kernel")

        image_type = get_bb_var('INITRAMFS_IMAGE')
        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        machine = get_bb_var('MACHINE')
        fitimage_its_path = os.path.join(deploy_dir_image,
                    "fitImage-its-%s-%s-%s" % (image_type, machine, machine))
        fitimage_path = os.path.join(deploy_dir_image,"fitImage")

        self.assertTrue(os.path.exists(fitimage_its_path),
            "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertTrue(os.path.exists(fitimage_path),
            "%s FIT image doesn't exist" % (fitimage_path))

        kernel_load = str(get_bb_var('UBOOT_LOADADDRESS'))
        kernel_entry = str(get_bb_var('UBOOT_ENTRYPOINT'))
        initramfs_bundle_format = str(get_bb_var('KERNEL_IMAGETYPE_REPLACEMENT'))
        uboot_arch = str(get_bb_var('UBOOT_ARCH'))
        initramfs_bundle = "arch/" + uboot_arch + "/boot/" + initramfs_bundle_format + ".initramfs"
        fit_hash_alg = str(get_bb_var('FIT_HASH_ALG'))

        its_file = open(fitimage_its_path)

        its_lines = [line.strip() for line in its_file.readlines()]

        exp_node_lines = [
            'kernel@1 {',
            'description = "Linux kernel";',
            'data = /incbin/("' + initramfs_bundle + '");',
            'type = "kernel";',
            'arch = "' + uboot_arch + '";',
            'os = "linux";',
            'compression = "none";',
            'load = <' + kernel_load + '>;',
            'entry = <' + kernel_entry + '>;',
            'hash@1 {',
            'algo = "' + fit_hash_alg +'";',
            '};',
            '};'
        ]

        node_str = exp_node_lines[0]

        test_passed = False

        print ("checking kernel node\n")

        if node_str in its_lines:
            node_start_idx = its_lines.index(node_str)
            node = its_lines[node_start_idx:(node_start_idx + len(exp_node_lines))]
            if node == exp_node_lines:
                print("kernel node verified")
            else:
                self.assertTrue(test_passed == True,"kernel node does not match expectation")

        rx_configs = re.compile("^conf@.*")
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
            rx_desc_line = re.compile("^description.*1 Linux kernel.*")
            if len(list(filter(rx_desc_line.match, node))) != 1:
                self.assertTrue(test_passed == True,"kernel keyword not found in the description line")
                break
            else:
                print("kernel keyword found in the description line")

            if 'kernel = "kernel@1";' not in node:
                self.assertTrue(test_passed == True,"kernel line not found")
                break
            else:
                print("kernel line found")

            rx_sign_line = re.compile("^sign-images.*kernel.*")
            if len(list(filter(rx_sign_line.match, node))) != 1:
                self.assertTrue(test_passed == True,"kernel hash not signed")
                break
            else:
                print("kernel hash signed")

            test_passed = True
            self.assertTrue(test_passed == True,"Initramfs bundle test success")
