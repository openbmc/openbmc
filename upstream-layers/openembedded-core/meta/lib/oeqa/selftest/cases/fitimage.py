#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import re
import shlex
import logging
import pprint
import tempfile

import oe.fitimage

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_vars, get_bb_var


class BbVarsMockGenKeys:
    def __init__(self, keydir, gen_keys="0", sign_enabled="0", keyname="", sign_ind="0", img_keyname=""):
        self.bb_vars = {
            'FIT_GENERATE_KEYS': gen_keys,
            'FIT_KEY_GENRSA_ARGS': "-F4",
            'FIT_KEY_REQ_ARGS': "-batch -new",
            'FIT_KEY_SIGN_PKCS': "-x509",
            'FIT_SIGN_INDIVIDUAL': sign_ind,
            'FIT_SIGN_NUMBITS': "2048",
            'UBOOT_SIGN_ENABLE': sign_enabled,
            'UBOOT_SIGN_IMG_KEYNAME': img_keyname,
            'UBOOT_SIGN_KEYDIR': keydir,
            'UBOOT_SIGN_KEYNAME': keyname,
        }

    def getVar(self, var):
        return self.bb_vars[var]

class FitImageTestCase(OESelftestTestCase):
    """Test functions usable for testing kernel-fitimage.bbclass and uboot-sign.bbclass

    A brief summary showing the structure of a test case:

    self._test_fitimage()
        # Generate a local.conf file and bitbake the bootloader or the kernel
        self._bitbake_fit_image()

        # Check if the its file contains the expected paths and attributes.
        # The _get_req_* functions are implemented by more specific chield classes.
        self._check_its_file()
            req_its_paths = self._get_req_its_paths()
            req_sigvalues_config = self._get_req_sigvalues_config()
            req_sigvalues_image = self._get_req_sigvalues_image()
            # Compare the its file against req_its_paths, req_sigvalues_config, req_sigvalues_image

        # Call the dumpimage utiliy and check that it prints all the expected paths and attributes
        # The _get_req_* functions are implemented by more specific chield classes.
        self._check_fitimage()
            self._get_req_sections()
            # Compare the output of the dumpimage utility against
    """

    MKIMAGE_HASH_LENGTHS = { 'sha256': 64, 'sha384': 96, 'sha512': 128 }
    MKIMAGE_SIGNATURE_LENGTHS = { 'rsa2048': 512 }

    def _gen_signing_key(self, bb_vars):
        """Generate a key pair and a singing certificate

        Generate a UBOOT_SIGN_KEYNAME in the UBOOT_SIGN_KEYDIR similar to what
        the FIT_GENERATE_KEYS feature does. However, having a static key is
        probably a more realistic use case than generating a random key with
        each clean build. So this needs to be tested as well.
        The FIT_GENERATE_KEYS generates 2 keys: The UBOOT_SIGN_KEYNAME and the
        UBOOT_SIGN_IMG_KEYNAME. The UBOOT_SIGN_IMG_KEYNAME is used by the
        FIT_SIGN_INDIVIDUAL feature only. Testing if everything is working if
        there is only one key available is important as well. Therefore this
        function generates only the keys which are really needed, not just two.
        """

        # Define some variables which are usually defined by the kernel-fitimage.bbclass.
        # But for testing purpose check if the uboot-sign.bbclass is independent from
        # the kernel-fitimage.bbclass
        fit_sign_numbits = bb_vars.get('FIT_SIGN_NUMBITS', "2048")
        fit_key_genrsa_args = bb_vars.get('FIT_KEY_GENRSA_ARGS', "-F4")
        fit_key_req_args =  bb_vars.get('FIT_KEY_REQ_ARGS', "-batch -new")
        fit_key_sign_pkcs = bb_vars.get('FIT_KEY_SIGN_PKCS', "-x509")

        uboot_sign_keydir = bb_vars['UBOOT_SIGN_KEYDIR']
        sign_keys = [bb_vars['UBOOT_SIGN_KEYNAME']]
        if bb_vars['FIT_SIGN_INDIVIDUAL'] == "1":
            sign_keys.append(bb_vars['UBOOT_SIGN_IMG_KEYNAME'])
        for sign_key in sign_keys:
            sing_key_path = os.path.join(uboot_sign_keydir, sign_key)
            if not os.path.isdir(uboot_sign_keydir):
                os.makedirs(uboot_sign_keydir)
            openssl_bindir = FitImageTestCase._setup_native('openssl-native')
            openssl_path = os.path.join(openssl_bindir, 'openssl')
            runCmd("%s genrsa %s -out %s.key %s" % (
                openssl_path,
                fit_key_genrsa_args,
                sing_key_path,
                fit_sign_numbits
            ))
            runCmd("%s req %s %s -key %s.key -out %s.crt" % (
                openssl_path,
                fit_key_req_args,
                fit_key_sign_pkcs,
                sing_key_path,
                sing_key_path
            ))

    @staticmethod
    def _gen_random_file(file_path, num_bytes=65536):
        with open(file_path, 'wb') as file_out:
            file_out.write(os.urandom(num_bytes))

    @staticmethod
    def _setup_native(native_recipe):
        """Build a native recipe and return the path to its bindir in RECIPE_SYSROOT_NATIVE"""
        bitbake(native_recipe + " -c addto_recipe_sysroot")
        vars = get_bb_vars(['RECIPE_SYSROOT_NATIVE', 'bindir'], native_recipe)
        return os.path.join(vars['RECIPE_SYSROOT_NATIVE'], vars['bindir'])

    def _verify_fit_image_signature(self, uboot_tools_bindir, fitimage_path, dtb_path, conf_name=None):
        """Verify the signature of a fit configuration

        The fit_check_sign utility from u-boot-tools-native is called.
        uboot-fit_check_sign -f fitImage -k $dtb_path -c conf-$dtb_name
        dtb_path refers to a binary device tree containing the public key.
        """
        fit_check_sign_path = os.path.join(uboot_tools_bindir, 'uboot-fit_check_sign')
        cmd = '%s -f %s -k %s' % (fit_check_sign_path, fitimage_path, dtb_path)
        if conf_name:
            cmd += ' -c %s' % conf_name
        result = runCmd(cmd)
        self.logger.debug("%s\nreturned: %s\n%s", cmd, str(result.status), result.output)
        self.assertIn("Signature check OK", result.output)

    def _verify_dtb_property(self, dtc_bindir, dtb_path, node_path, property_name, req_property, absent=False):
        """Verify device tree properties

        The fdtget utility from dtc-native is called and the property is compared.
        """
        fdtget_path = os.path.join(dtc_bindir, 'fdtget')
        cmd = '%s %s %s %s' % (fdtget_path, dtb_path, node_path, property_name)
        if absent:
            result = runCmd(cmd, ignore_status=True)
            self.logger.debug("%s\nreturned: %s\n%s", cmd, str(result.status), result.output)
            self.assertIn("FDT_ERR_NOTFOUND", result.output)
        else:
            result = runCmd(cmd)
            self.logger.debug("%s\nreturned: %s\n%s", cmd, str(result.status), result.output)
            self.assertEqual(req_property, result.output.strip())

    @staticmethod
    def _find_string_in_bin_file(file_path, search_string):
        """find strings in a binary file

        Shell equivalent: strings "$1" | grep "$2" | wc -l
        return number of matches
        """
        found_positions = 0
        with open(file_path, 'rb') as file:
            content = file.read().decode('ascii', errors='ignore')
            found_positions = content.count(search_string)
        return found_positions

    @staticmethod
    def _get_uboot_mkimage_sign_args(uboot_mkimage_sign_args):
        """Retrive the string passed via -c to the mkimage command

        Example: If a build configutation defines
          UBOOT_MKIMAGE_SIGN_ARGS = "-c 'a smart comment'"
        this function returns "a smart comment"
        """
        a_comment = None
        if uboot_mkimage_sign_args:
            mkimage_args = shlex.split(uboot_mkimage_sign_args)
            try:
                c_index = mkimage_args.index('-c')
                a_comment = mkimage_args[c_index+1]
            except ValueError:
                pass
        return a_comment

    @staticmethod
    def _get_dtb_files(bb_vars):
        """Return a list of devicetree names

        The list should be used to check the dtb and conf nodes in the FIT image or its file.
        In addition to the entries from KERNEL_DEVICETREE, the external devicetree and the
        external devicetree overlay added by the test recipe bbb-dtbs-as-ext are handled as well.
        """
        kernel_devicetree = bb_vars.get('KERNEL_DEVICETREE')
        all_dtbs = []
        dtb_symlinks = []
        if kernel_devicetree:
            all_dtbs += [os.path.basename(dtb) for dtb in kernel_devicetree.split()]
        # Support only the test recipe which provides 1 devicetree and 1 devicetree overlay
        pref_prov_dtb = bb_vars.get('PREFERRED_PROVIDER_virtual/dtb')
        if pref_prov_dtb == "bbb-dtbs-as-ext":
            all_dtbs += ["am335x-bonegreen-ext.dtb", "BBORG_RELAY-00A2.dtbo"]
            dtb_symlinks.append("am335x-bonegreen-ext-alias.dtb")
        return (all_dtbs, dtb_symlinks)

    def _is_req_dict_in_dict(self, found_dict, req_dict):
        """
        Check if all key-value pairs in the required dictionary are present in the found dictionary.

        This function recursively checks if the required dictionary (`req_dict`) is a subset of the found dictionary (`found_dict`).
        It supports nested dictionaries, strings, lists, and sets as values.

        Args:
            found_dict (dict): The dictionary to search within.
            req_dict (dict): The dictionary containing the required key-value pairs.
        """
        for key, value in req_dict.items():
            self.assertIn(key, found_dict)
            if isinstance(value, dict):
                self._is_req_dict_in_dict(found_dict[key], value)
            elif isinstance(value, str):
                self.assertIn(value, found_dict[key])
            elif isinstance(value, list):
                self.assertLessEqual(set(value), set(found_dict[key]))
            elif isinstance(value, set):
                self.assertLessEqual(value, found_dict[key])
            else:
                self.assertEqual(value, found_dict[key])

    def _check_its_file(self, bb_vars, its_file_path):
        """Check if the its file contains the expected sections and fields"""
        # print the its file for debugging
        if logging.DEBUG >= self.logger.level:
            with open(its_file_path) as its_file:
                self.logger.debug("its file: %s" % its_file.read())

        # Generate a list of expected paths in the its file
        req_its_paths = self._get_req_its_paths(bb_vars)
        self.logger.debug("req_its_paths:\n%s\n" % pprint.pformat(req_its_paths, indent=4))

        # Generate a dict of expected configuration signature nodes
        req_sigvalues_config = self._get_req_sigvalues_config(bb_vars)
        self.logger.debug("req_sigvalues_config:\n%s\n" % pprint.pformat(req_sigvalues_config, indent=4))

        # Generate a dict of expected image signature nodes
        req_sigvalues_image = self._get_req_sigvalues_image(bb_vars)
        self.logger.debug("req_sigvalues_image:\n%s\n" % pprint.pformat(req_sigvalues_image, indent=4))

        # Parse the its file for paths and signatures
        its_path = []
        its_paths = []
        linect = 0
        sigs = {}
        with open(its_file_path) as its_file:
            for line in its_file:
                linect += 1
                line = line.strip()
                if line.endswith('};'):
                    its_path.pop()
                elif line.endswith('{'):
                    its_path.append(line[:-1].strip())
                    its_paths.append(its_path[:])
                # kernel-fitimage uses signature-1, uboot-sign uses signature
                elif its_path and (its_path[-1] == 'signature-1' or its_path[-1] == 'signature'):
                    itsdotpath = '.'.join(its_path)
                    if not itsdotpath in sigs:
                        sigs[itsdotpath] = {}
                    if not '=' in line or not line.endswith(';'):
                        self.fail('Unexpected formatting in %s sigs section line %d:%s' % (its_file_path, linect, line))
                    key, value = line.split('=', 1)
                    sigs[itsdotpath][key.rstrip()] = value.lstrip().rstrip(';')

        # Check if all expected paths are found in the its file
        self.logger.debug("itspaths:\n%s\n" % pprint.pformat(its_paths, indent=4))
        for req_path in req_its_paths:
            if not req_path in its_paths:
                self.fail('Missing path in its file: %s (%s)' % (req_path, its_file_path))

        # Check if all the expected singnature nodes (images and configurations) are found
        self.logger.debug("sigs:\n%s\n" % pprint.pformat(sigs, indent=4))
        if req_sigvalues_config or req_sigvalues_image:
            for its_path, values in sigs.items():
                if bb_vars.get('FIT_CONF_PREFIX', "conf-") in its_path:
                    reqsigvalues = req_sigvalues_config
                else:
                    reqsigvalues = req_sigvalues_image
                for reqkey, reqvalue in reqsigvalues.items():
                    value = values.get(reqkey, None)
                    if value is None:
                        self.fail('Missing key "%s" in its file signature section %s (%s)' % (reqkey, its_path, its_file_path))
                    self.assertEqual(value, reqvalue)

        # Generate a list of expected fields in the its file
        req_its_fields = self._get_req_its_fields(bb_vars)
        self.logger.debug("req_its_fields:\n%s\n" % pprint.pformat(req_its_fields, indent=4))

        # Check if all expected fields are in the its file
        if req_its_fields:
            field_index = 0
            field_index_last = len(req_its_fields) - 1
            with open(its_file_path) as its_file:
                for line in its_file:
                    if req_its_fields[field_index] in line:
                        if field_index < field_index_last:
                            field_index +=1
                        else:
                            break
            self.assertEqual(field_index, field_index_last,
                "Fields in Image Tree Source File %s did not match, error in finding %s"
                % (its_file_path, req_its_fields[field_index]))

    def _check_fitimage(self, bb_vars, fitimage_path, uboot_tools_bindir):
        """Run dumpimage on the final FIT image and parse the output into a dict"""
        dumpimage_path = os.path.join(uboot_tools_bindir, 'dumpimage')
        cmd = '%s -l %s' % (dumpimage_path, fitimage_path)
        self.logger.debug("Analyzing output from dumpimage: %s" % cmd)
        dumpimage_result = runCmd(cmd)
        in_section = None
        sections = {}
        self.logger.debug("dumpimage output: %s" % dumpimage_result.output)
        for line in dumpimage_result.output.splitlines():
            # Find potentially hashed and signed sections
            if line.startswith((' Configuration', ' Image')):
                in_section = re.search(r'\((.*)\)', line).groups()[0]
            # Key value lines start with two spaces otherwise the section ended
            elif not line.startswith("  "):
                in_section = None
            # Handle key value lines of this section
            elif in_section:
                if not in_section in sections:
                    sections[in_section] = {}
                try:
                    key, value = line.split(':', 1)
                    key = key.strip()
                    value = value.strip()
                except ValueError as val_err:
                    # Handle multiple entries as e.g. for Loadables as a list
                    if key and line.startswith("   "):
                        value = sections[in_section][key] + "," + line.strip()
                    else:
                        raise ValueError(f"Error processing line: '{line}'. Original error: {val_err}")
                sections[in_section][key] = value

        # Check if the requested dictionary is a subset of the parsed dictionary
        req_sections, num_signatures = self._get_req_sections(bb_vars)
        self.logger.debug("req_sections: \n%s\n" % pprint.pformat(req_sections, indent=4))
        self.logger.debug("dumpimage sections: \n%s\n" % pprint.pformat(sections, indent=4))
        self._is_req_dict_in_dict(sections, req_sections)

        # Call the signing related checks if the function is provided by a inherited class
        self._check_signing(bb_vars, sections, num_signatures, uboot_tools_bindir, fitimage_path)

    def _get_req_its_paths(self, bb_vars):
        self.logger.error("This function needs to be implemented")
        return []

    def _get_req_its_fields(self, bb_vars):
        self.logger.error("This function needs to be implemented")
        return []

    def _get_req_sigvalues_config(self, bb_vars):
        self.logger.error("This function needs to be implemented")
        return {}

    def _get_req_sigvalues_image(self, bb_vars):
        self.logger.error("This function needs to be implemented")
        return {}

    def _get_req_sections(self, bb_vars):
        self.logger.error("This function needs to be implemented")
        return ({}, 0)

    def _check_signing(self, bb_vars, sections, num_signatures, uboot_tools_bindir, fitimage_path):
        """Verify the signatures in the FIT image."""
        self.fail("Function needs to be implemented by inheriting classes")

    def _bitbake_fit_image(self, bb_vars):
        """Bitbake the FIT image and return the paths to the its file and the FIT image"""
        self.fail("Function needs to be implemented by inheriting classes")

    def _test_fitimage(self, bb_vars):
        """Check if the its file and the FIT image are created and signed correctly"""
        fitimage_its_path, fitimage_path = self._bitbake_fit_image(bb_vars)
        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.assertExists(fitimage_path, "%s FIT image doesn't exist" % (fitimage_path))

        self.logger.debug("Checking its: %s" % fitimage_its_path)
        self._check_its_file(bb_vars, fitimage_its_path)

        # Setup u-boot-tools-native
        uboot_tools_bindir = FitImageTestCase._setup_native('u-boot-tools-native')

        # Verify the FIT image
        self._check_fitimage(bb_vars, fitimage_path, uboot_tools_bindir)

class KernelFitImageBase(FitImageTestCase):
    """Test cases for the linux-yocto-fitimage recipe"""

    def _fit_get_bb_vars(self, additional_vars=[]):
        """Retrieve BitBake variables specific to the test case.

        Call the get_bb_vars function once and get all variables needed by the test case.
        """
        internal_used = {
            'DEPLOY_DIR_IMAGE',
            'FIT_CONF_DEFAULT_DTB',
            'FIT_CONF_PREFIX',
            'FIT_DESC',
            'FIT_HASH_ALG',
            'FIT_KERNEL_COMP_ALG',
            'FIT_SIGN_ALG',
            'FIT_SIGN_INDIVIDUAL',
            'FIT_UBOOT_ENV',
            'INITRAMFS_IMAGE_BUNDLE',
            'INITRAMFS_IMAGE_NAME',
            'INITRAMFS_IMAGE',
            'KERNEL_DEPLOYSUBDIR',
            'KERNEL_DEVICETREE',
            'KERNEL_FIT_LINK_NAME',
            'MACHINE',
            'PREFERRED_PROVIDER_virtual/dtb',
            'UBOOT_ARCH',
            'UBOOT_ENTRYPOINT',
            'UBOOT_LOADADDRESS',
            'UBOOT_MKIMAGE_KERNEL_TYPE',
            'UBOOT_MKIMAGE_SIGN_ARGS',
            'UBOOT_RD_ENTRYPOINT',
            'UBOOT_RD_LOADADDRESS',
            'UBOOT_SIGN_ENABLE',
            'UBOOT_SIGN_IMG_KEYNAME',
            'UBOOT_SIGN_KEYDIR',
            'UBOOT_SIGN_KEYNAME',
        }
        bb_vars = get_bb_vars(list(internal_used | set(additional_vars)), self.kernel_recipe)
        self.logger.debug("bb_vars: %s" % pprint.pformat(bb_vars, indent=4))
        return bb_vars

    def _config_add_kernel_classes(self, config):
        config += '# Use kernel-fit-extra-artifacts.bbclass for the creation of the vmlinux artifact' + os.linesep
        config += 'KERNEL_CLASSES = "kernel-fit-extra-artifacts"' + os.linesep
        return config

    @property
    def kernel_recipe(self):
        return "linux-yocto-fitimage"

    def _config_add_uboot_env(self, config):
        """Generate an u-boot environment

        Create a boot.cmd file that is packed into the FIT image as a source-able text file.
        Updates the configuration to include the boot.cmd file.
        """
        fit_uenv_file =  "boot.cmd"
        test_files_dir = "test-files"
        fit_uenv_path = os.path.join(self.builddir, test_files_dir, fit_uenv_file)

        config += '# Add an u-boot script to the fitImage' + os.linesep
        config += 'FIT_UBOOT_ENV = "%s"' % fit_uenv_file + os.linesep
        config += 'FILESEXTRAPATHS:prepend := "${TOPDIR}/%s:"' % test_files_dir + os.linesep
        config += 'SRC_URI:append:pn-%s = " file://${FIT_UBOOT_ENV}"' % self.kernel_recipe + os.linesep

        if not os.path.isdir(test_files_dir):
            os.makedirs(test_files_dir)
        self.logger.debug("Writing to: %s" % fit_uenv_path)
        with open(fit_uenv_path, "w") as f:
            f.write('echo "hello world"')

        return config

    def _bitbake_fit_image(self, bb_vars):
        """Bitbake the kernel and return the paths to the its file and the FIT image"""
        bitbake(self.kernel_recipe)

        # Find the right its file and the final fitImage and check if both files are available
        deploy_dir_image = bb_vars['DEPLOY_DIR_IMAGE']
        initramfs_image = bb_vars['INITRAMFS_IMAGE']
        initramfs_image_bundle = bb_vars['INITRAMFS_IMAGE_BUNDLE']
        initramfs_image_name = bb_vars['INITRAMFS_IMAGE_NAME']
        kernel_fit_link_name = bb_vars['KERNEL_FIT_LINK_NAME']
        if not initramfs_image and initramfs_image_bundle != "1":
            fitimage_its_name = "fitImage-its-%s" % kernel_fit_link_name
            fitimage_name = "fitImage"
        elif initramfs_image and initramfs_image_bundle != "1":
            fitimage_its_name = "fitImage-its-%s-%s" % (initramfs_image_name, kernel_fit_link_name)
            fitimage_name = "fitImage-%s-%s" % (initramfs_image_name, kernel_fit_link_name)
        elif initramfs_image and initramfs_image_bundle == "1":
            fitimage_its_name = "fitImage-its-%s-%s" % (initramfs_image_name, kernel_fit_link_name)
            fitimage_name = "fitImage"  # or fitImage-${KERNEL_IMAGE_LINK_NAME}${KERNEL_IMAGE_BIN_EXT}
        else:
            self.fail('Invalid configuration: INITRAMFS_IMAGE_BUNDLE = "1" and not INITRAMFS_IMAGE')
        kernel_deploysubdir = bb_vars['KERNEL_DEPLOYSUBDIR']
        if kernel_deploysubdir:
            fitimage_its_path = os.path.realpath(os.path.join(deploy_dir_image, kernel_deploysubdir, fitimage_its_name))
            fitimage_path = os.path.realpath(os.path.join(deploy_dir_image, kernel_deploysubdir, fitimage_name))
        else:
            fitimage_its_path = os.path.realpath(os.path.join(deploy_dir_image, fitimage_its_name))
            fitimage_path = os.path.realpath(os.path.join(deploy_dir_image, fitimage_name))
        return (fitimage_its_path, fitimage_path)

    def _get_req_its_paths(self, bb_vars):
        """Generate a list of expected paths in the its file

        Example:
            [
                ['/', 'images', 'kernel-1', 'hash-1'],
                ['/', 'images', 'kernel-1', 'signature-1'],
            ]
        """
        dtb_files, dtb_symlinks = FitImageTestCase._get_dtb_files(bb_vars)
        fit_sign_individual = bb_vars['FIT_SIGN_INDIVIDUAL']
        fit_uboot_env = bb_vars['FIT_UBOOT_ENV']
        initramfs_image = bb_vars['INITRAMFS_IMAGE']
        initramfs_image_bundle = bb_vars['INITRAMFS_IMAGE_BUNDLE']
        uboot_sign_enable = bb_vars.get('UBOOT_SIGN_ENABLE')

        # image nodes
        images = [ 'kernel-1' ]
        if dtb_files:
            images += [ 'fdt-' + dtb for dtb in dtb_files ]
        if fit_uboot_env:
            images.append('bootscr-' + fit_uboot_env)
        if bb_vars['MACHINE'] == "qemux86-64": # Not really the right if
            images.append('setup-1')
        if initramfs_image and initramfs_image_bundle != "1":
            images.append('ramdisk-1')

        # configuration nodes (one per DTB and also one per symlink)
        if dtb_files:
            configurations = [bb_vars['FIT_CONF_PREFIX'] + conf for conf in dtb_files + dtb_symlinks]
        else:
            configurations = [bb_vars['FIT_CONF_PREFIX'] + '1']

        # Create a list of paths for all image and configuration nodes
        req_its_paths = []
        for image in images:
            req_its_paths.append(['/', 'images', image, 'hash-1'])
            if uboot_sign_enable == "1" and fit_sign_individual == "1":
                req_its_paths.append(['/', 'images', image, 'signature-1'])
        for configuration in configurations:
            req_its_paths.append(['/', 'configurations', configuration, 'hash-1'])
            if uboot_sign_enable == "1":
                req_its_paths.append(['/', 'configurations', configuration, 'signature-1'])
        return req_its_paths

    def _get_req_its_fields(self, bb_vars):
        initramfs_image = bb_vars['INITRAMFS_IMAGE']
        initramfs_image_bundle = bb_vars['INITRAMFS_IMAGE_BUNDLE']
        uboot_rd_loadaddress = bb_vars.get('UBOOT_RD_LOADADDRESS')
        uboot_rd_entrypoint = bb_vars.get('UBOOT_RD_ENTRYPOINT')

        its_field_check = [
            'description = "%s";' % bb_vars['FIT_DESC'],
            'description = "Linux kernel";',
            'type = "' + str(bb_vars['UBOOT_MKIMAGE_KERNEL_TYPE']) + '";',
            # 'compression = "' + str(bb_vars['FIT_KERNEL_COMP_ALG']) + '";', defined based on files in TMPDIR, not ideal...
            'data = /incbin/("linux.bin");',
            'arch = "' + str(bb_vars['UBOOT_ARCH']) + '";',
            'os = "linux";',
            'load = <' + str(bb_vars['UBOOT_LOADADDRESS']) + '>;',
            'entry = <' + str(bb_vars['UBOOT_ENTRYPOINT']) + '>;',
        ]
        if initramfs_image and initramfs_image_bundle != "1":
            its_field_check.append('type = "ramdisk";')
            if uboot_rd_loadaddress:
                its_field_check.append("load = <%s>;" % uboot_rd_loadaddress)
            if uboot_rd_entrypoint:
                its_field_check.append("entry = <%s>;" % uboot_rd_entrypoint)

        fit_conf_default_dtb = bb_vars.get('FIT_CONF_DEFAULT_DTB')
        if fit_conf_default_dtb:
            fit_conf_prefix = bb_vars.get('FIT_CONF_PREFIX', "conf-")
            its_field_check.append('default = "' + fit_conf_prefix + fit_conf_default_dtb + '";')

        its_field_check.append('kernel = "kernel-1";')

        if initramfs_image and initramfs_image_bundle != "1":
            its_field_check.append('ramdisk = "ramdisk-1";')

        return its_field_check

    def _get_req_sigvalues_config(self, bb_vars):
        """Generate a dictionary of expected configuration signature nodes"""
        if bb_vars.get('UBOOT_SIGN_ENABLE') != "1":
            return {}
        sign_images = '"kernel", "fdt"'
        if bb_vars['INITRAMFS_IMAGE'] and bb_vars['INITRAMFS_IMAGE_BUNDLE'] != "1":
            sign_images += ', "ramdisk"'
        if bb_vars['FIT_UBOOT_ENV']:
            sign_images += ', "bootscr"'
        req_sigvalues_config = {
            'algo': '"%s,%s"' % (bb_vars['FIT_HASH_ALG'], bb_vars['FIT_SIGN_ALG']),
            'key-name-hint': '"%s"' % bb_vars['UBOOT_SIGN_KEYNAME'],
            'sign-images': sign_images,
        }
        return req_sigvalues_config

    def _get_req_sigvalues_image(self, bb_vars):
        """Generate a dictionary of expected image signature nodes"""
        if bb_vars['FIT_SIGN_INDIVIDUAL'] != "1":
            return {}
        req_sigvalues_image = {
            'algo': '"%s,%s"' % (bb_vars['FIT_HASH_ALG'], bb_vars['FIT_SIGN_ALG']),
            'key-name-hint': '"%s"' % bb_vars['UBOOT_SIGN_IMG_KEYNAME'],
        }
        return req_sigvalues_image

    def _get_req_sections(self, bb_vars):
        """Generate a dictionary of expected sections in the output of dumpimage"""
        dtb_files, dtb_symlinks = FitImageTestCase._get_dtb_files(bb_vars)
        fit_hash_alg = bb_vars['FIT_HASH_ALG']
        fit_sign_alg = bb_vars['FIT_SIGN_ALG']
        fit_sign_individual = bb_vars['FIT_SIGN_INDIVIDUAL']
        fit_uboot_env = bb_vars['FIT_UBOOT_ENV']
        initramfs_image = bb_vars['INITRAMFS_IMAGE']
        initramfs_image_bundle = bb_vars['INITRAMFS_IMAGE_BUNDLE']
        uboot_sign_enable = bb_vars['UBOOT_SIGN_ENABLE']
        uboot_sign_img_keyname = bb_vars['UBOOT_SIGN_IMG_KEYNAME']
        uboot_sign_keyname = bb_vars['UBOOT_SIGN_KEYNAME']
        num_signatures = 0
        req_sections = {
            "kernel-1": {
                "Type": "Kernel Image",
                "OS": "Linux",
                "Load Address": bb_vars['UBOOT_LOADADDRESS'],
                "Entry Point": bb_vars['UBOOT_ENTRYPOINT'],
            }
        }
        # Create one section per DTB
        for dtb in dtb_files:
            req_sections['fdt-' + dtb] = {
                "Type": "Flat Device Tree",
            }
        # Add a script section if there is a script
        if fit_uboot_env:
            req_sections['bootscr-' + fit_uboot_env] = { "Type": "Script" }
        # Add the initramfs
        if initramfs_image and initramfs_image_bundle != "1":
            req_sections['ramdisk-1'] = {
                "Type": "RAMDisk Image",
                "Load Address": bb_vars['UBOOT_RD_LOADADDRESS'],
                "Entry Point": bb_vars['UBOOT_RD_ENTRYPOINT']
            }
        # Create a configuration section for each DTB
        if dtb_files:
            for dtb in dtb_files + dtb_symlinks:
                conf_name = bb_vars['FIT_CONF_PREFIX'] + dtb
                # Assume that DTBs with an "-alias" in its name are symlink DTBs created e.g. by the
                # bbb-dtbs-as-ext test recipe. Make the configuration node pointing to the real DTB.
                real_dtb = dtb.replace("-alias", "")
                # dtb overlays do not refer to a kernel (yet?)
                if dtb.endswith('.dtbo'):
                    req_sections[conf_name] = {
                        "FDT": 'fdt-' + real_dtb,
                    }
                else:
                    req_sections[conf_name] = {
                        "Kernel": "kernel-1",
                        "FDT": 'fdt-' + real_dtb,
                    }
                if initramfs_image and initramfs_image_bundle != "1":
                    req_sections[conf_name]['Init Ramdisk'] = "ramdisk-1"
        else:
            conf_name = bb_vars['FIT_CONF_PREFIX'] +  '1'
            req_sections[conf_name] = {
                "Kernel": "kernel-1"
            }
            if initramfs_image and initramfs_image_bundle != "1":
                req_sections[conf_name]['Init Ramdisk'] = "ramdisk-1"

        # Add signing related properties if needed
        if uboot_sign_enable == "1":
            for section in req_sections:
                req_sections[section]['Hash algo'] = fit_hash_alg
                if section.startswith(bb_vars['FIT_CONF_PREFIX']):
                    req_sections[section]['Hash value'] = "unavailable"
                    req_sections[section]['Sign algo'] = "%s,%s:%s" % (fit_hash_alg, fit_sign_alg, uboot_sign_keyname)
                    num_signatures += 1
                elif fit_sign_individual == "1":
                    req_sections[section]['Sign algo'] = "%s,%s:%s" % (fit_hash_alg, fit_sign_alg, uboot_sign_img_keyname)
                    num_signatures += 1
        return (req_sections, num_signatures)

    def _check_signing(self, bb_vars, sections, num_signatures, uboot_tools_bindir, fitimage_path):
        """Verify the signature nodes in the FIT image"""
        if bb_vars['UBOOT_SIGN_ENABLE'] == "1":
            self.logger.debug("Verifying signatures in the FIT image")
        else:
            self.logger.debug("FIT image is not signed. Signature verification is not needed.")
            return

        fit_hash_alg = bb_vars['FIT_HASH_ALG']
        fit_sign_alg = bb_vars['FIT_SIGN_ALG']
        uboot_sign_keyname = bb_vars['UBOOT_SIGN_KEYNAME']
        uboot_sign_img_keyname = bb_vars['UBOOT_SIGN_IMG_KEYNAME']
        deploy_dir_image = bb_vars['DEPLOY_DIR_IMAGE']
        kernel_deploysubdir = bb_vars['KERNEL_DEPLOYSUBDIR']
        fit_sign_individual = bb_vars['FIT_SIGN_INDIVIDUAL']
        fit_hash_alg_len = FitImageTestCase.MKIMAGE_HASH_LENGTHS[fit_hash_alg]
        fit_sign_alg_len = FitImageTestCase.MKIMAGE_SIGNATURE_LENGTHS[fit_sign_alg]
        for section, values in sections.items():
            # Configuration nodes are always signed with UBOOT_SIGN_KEYNAME (if UBOOT_SIGN_ENABLE = "1")
            if section.startswith(bb_vars['FIT_CONF_PREFIX']):
                sign_algo = values.get('Sign algo', None)
                req_sign_algo = "%s,%s:%s" % (fit_hash_alg, fit_sign_alg, uboot_sign_keyname)
                self.assertEqual(sign_algo, req_sign_algo, 'Signature algorithm for %s not expected value' % section)
                sign_value = values.get('Sign value', None)
                self.assertEqual(len(sign_value), fit_sign_alg_len, 'Signature value for section %s not expected length' % section)
                dtb_file_name = section.replace(bb_vars['FIT_CONF_PREFIX'], '')
                dtb_path = os.path.join(deploy_dir_image, dtb_file_name)
                if kernel_deploysubdir:
                    dtb_path = os.path.join(deploy_dir_image, kernel_deploysubdir, dtb_file_name)
                # External devicetrees created by devicetree.bbclass are in a subfolder and have priority
                dtb_path_ext = os.path.join(deploy_dir_image, "devicetree", dtb_file_name)
                if os.path.exists(dtb_path_ext):
                    dtb_path = dtb_path_ext
                self._verify_fit_image_signature(uboot_tools_bindir, fitimage_path, dtb_path, section)
            else:
                # Image nodes always need a hash which gets indirectly signed by the config signature
                hash_algo = values.get('Hash algo', None)
                self.assertEqual(hash_algo, fit_hash_alg)
                hash_value = values.get('Hash value', None)
                self.assertEqual(len(hash_value), fit_hash_alg_len, 'Hash value for section %s not expected length' % section)
                # Optionally, if FIT_SIGN_INDIVIDUAL = 1 also the image nodes have a signature (which is redundant but possible)
                if fit_sign_individual == "1":
                    sign_algo = values.get('Sign algo', None)
                    req_sign_algo = "%s,%s:%s" % (fit_hash_alg, fit_sign_alg, uboot_sign_img_keyname)
                    self.assertEqual(sign_algo, req_sign_algo, 'Signature algorithm for %s not expected value' % section)
                    sign_value = values.get('Sign value', None)
                    self.assertEqual(len(sign_value), fit_sign_alg_len, 'Signature value for section %s not expected length' % section)

        # Search for the string passed to mkimage in each signed section of the FIT image.
        # Looks like mkimage supports to add a comment but does not support to read it back.
        a_comment = FitImageTestCase._get_uboot_mkimage_sign_args(bb_vars['UBOOT_MKIMAGE_SIGN_ARGS'])
        self.logger.debug("a_comment: %s" % a_comment)
        if a_comment:
            found_comments = FitImageTestCase._find_string_in_bin_file(fitimage_path, a_comment)
            self.assertEqual(found_comments, num_signatures, "Expected %d signed and commented (%s) sections in the fitImage." %
                             (num_signatures, a_comment))

class KernelFitImageRecipeTests(KernelFitImageBase):
    """Test cases for the kernel-fitimage bbclass"""

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
KERNEL_IMAGETYPE = "Image"

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
FIT_CONF_PREFIX = "foo-"
"""
        config = self._config_add_kernel_classes(config)
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()
        self._test_fitimage(bb_vars)

    def test_get_compatible_from_dtb(self):
        """Test the oe.fitimage.get_compatible_from_dtb function

        1. bitbake bbb-dtbs-as-ext
        2. Check if symlink_points_below returns the path to the DTB
        3. Check if the expected compatible string is found by get_compatible_from_dtb()
        """
        DTB_RECIPE = "bbb-dtbs-as-ext"
        DTB_FILE = "am335x-bonegreen-ext.dtb"
        DTB_SYMLINK = "am335x-bonegreen-ext-alias.dtb"
        DTBO_FILE = "BBORG_RELAY-00A2.dtbo"
        EXPECTED_COMP = ["ti,am335x-bone-green", "ti,am335x-bone-black", "ti,am335x-bone", "ti,am33xx"]

        config = """
DISTRO = "poky"
MACHINE = "beaglebone-yocto"
"""
        self.write_config(config)

        # Provide the fdtget command called by get_compatible_from_dtb
        dtc_bindir = FitImageTestCase._setup_native('dtc-native')
        fdtget_path = os.path.join(dtc_bindir, "fdtget")
        self.assertExists(fdtget_path)

        # bitbake an external DTB with a symlink to it and a DTB overlay
        bitbake(DTB_RECIPE)
        deploy_dir_image = get_bb_var("DEPLOY_DIR_IMAGE", DTB_RECIPE)
        devicetree_dir = os.path.join(deploy_dir_image, "devicetree")
        dtb_path = os.path.join(devicetree_dir, DTB_FILE)
        dtb_alias_path = os.path.join(devicetree_dir, DTB_SYMLINK)
        dtbo_file = os.path.join(devicetree_dir, DTBO_FILE)
        self.assertExists(dtb_path)
        self.assertExists(dtb_alias_path)
        self.assertExists(dtbo_file)

        # Test symlink_points_below
        linked_dtb = oe.fitimage.symlink_points_below(dtb_alias_path, devicetree_dir)
        self.assertEqual(linked_dtb, DTB_FILE)

        # Check if get_compatible_from_dtb finds the expected compatible string in the DTBs
        comp = oe.fitimage.get_compatible_from_dtb(dtb_path, fdtget_path)
        self.assertEqual(comp, EXPECTED_COMP)
        comp_alias = oe.fitimage.get_compatible_from_dtb(dtb_alias_path, fdtget_path)
        self.assertEqual(comp_alias, EXPECTED_COMP)
        # The alias is a symlink, therefore the compatible string is equal
        self.assertEqual(comp_alias, comp)

    def test_fit_image_ext_dtb_dtbo(self):
        """
        Summary:     Check if FIT image and Image Tree Source (its) are created correctly.
        Expected:    1) its and FIT image are built successfully
                     2) The its file contains also the external devicetree overlay
                     3) Dumping the FIT image indicates the devicetree overlay
        """
        config = """
# Enable creation of fitImage
MACHINE = "beaglebone-yocto"
# Add a devicetree overlay which does not need kernel sources
PREFERRED_PROVIDER_virtual/dtb = "bbb-dtbs-as-ext"
"""
        config = self._config_add_kernel_classes(config)
        config = self._config_add_uboot_env(config)
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()
        self._test_fitimage(bb_vars)


    def test_sign_fit_image_configurations(self):
        """
        Summary:     Check if FIT image and Image Tree Source (its) are created
                     and the configuration nodes are signed correctly.
        Expected:    1) its and FIT image are built successfully
                     2) Scanning the its file indicates signing is enabled
                        as requested by UBOOT_SIGN_ENABLE
                     3) Dumping the FIT image indicates signature values
                        are present (only for the configuration nodes as
                        FIT_SIGN_INDIVIDUAL is disabled)
                     4) Verify the FIT image contains the comments passed via
                        UBOOT_MKIMAGE_SIGN_ARGS once per configuration node.
        """
        # Generate a configuration section which gets included into the local.conf file
        config = """
# Enable creation of fitImage
MACHINE = "beaglebone-yocto"
UBOOT_SIGN_ENABLE = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_KEYNAME = "dev"
UBOOT_MKIMAGE_SIGN_ARGS = "-c 'a smart comment'"
FIT_CONF_DEFAULT_DTB = "am335x-bonegreen.dtb"
"""
        config = self._config_add_kernel_classes(config)
        config = self._config_add_uboot_env(config)
        self.write_config(config)

        # Retrieve some variables from bitbake
        bb_vars = self._fit_get_bb_vars([
            'FIT_KEY_GENRSA_ARGS',
            'FIT_KEY_REQ_ARGS',
            'FIT_KEY_SIGN_PKCS',
            'FIT_SIGN_NUMBITS',
            'UBOOT_SIGN_KEYDIR',
        ])

        self._gen_signing_key(bb_vars)
        self._test_fitimage(bb_vars)

    def test_sign_fit_image_individual(self):
        """
        Summary:     Check if FIT image and Image Tree Source (its) are created
                     and all nodes are signed correctly.
        Expected:    1) its and FIT image are built successfully
                     2) Scanning the its file indicates signing is enabled
                        as requested by UBOOT_SIGN_ENABLE
                     3) Dumping the FIT image indicates signature values
                        are present (including for images as enabled via
                        FIT_SIGN_INDIVIDUAL)
                        This also implies that FIT_GENERATE_KEYS = "1" works.
                     4) Verify the FIT image contains the comments passed via
                        UBOOT_MKIMAGE_SIGN_ARGS once per image and per
                        configuration node.
        Note:        This test is mostly for backward compatibility.
                     The recommended approach is to sign the configuration nodes
                     which include also the hashes of all the images. Signing
                     all the images individually is therefore redundant.
        Product:     oe-core
        Author:      Paul Eggleton <paul.eggleton@microsoft.com> based upon
                     work by Usama Arif <usama.arif@arm.com>
        """
        # Generate a configuration section which gets included into the local.conf file
        config = """
# Enable creation of fitImage
MACHINE = "beaglebone-yocto"
UBOOT_SIGN_ENABLE = "1"
FIT_GENERATE_KEYS = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_IMG_KEYNAME = "img-oe-selftest"
UBOOT_SIGN_KEYNAME = "cfg-oe-selftest"
FIT_SIGN_INDIVIDUAL = "1"
UBOOT_MKIMAGE_SIGN_ARGS = "-c 'a smart comment'"
"""
        config = self._config_add_kernel_classes(config)
        config = self._config_add_uboot_env(config)
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()

        # Ensure new keys are generated and FIT_GENERATE_KEYS = "1" is tested
        bitbake("kernel-signing-keys-native -c compile -f")

        self._test_fitimage(bb_vars)

    def test_fit_image_sign_initramfs(self):
        """
        Summary:     Verifies the content of the initramfs node in the FIT Image Tree Source (its)
                     The FIT settings are set by the test case.
                     The machine used is beaglebone-yocto.
        Expected:    1. The ITS is generated with initramfs support
                     2. All the fields in the kernel node are as expected (matching the
                        conf settings)
                     3. The kernel is included in all the available configurations and
                        its hash is included in the configuration signature

        Product:     oe-core
        Author:      Abdellatif El Khlifi <abdellatif.elkhlifi@arm.com>
        """

        config = """
DISTRO = "poky"
MACHINE = "beaglebone-yocto"
INITRAMFS_IMAGE = "core-image-minimal-initramfs"
INITRAMFS_SCRIPTS = ""
UBOOT_MACHINE = "am335x_evm_defconfig"
UBOOT_SIGN_ENABLE = "1"
UBOOT_SIGN_KEYNAME = "beaglebonekey"
UBOOT_SIGN_KEYDIR ?= "${DEPLOY_DIR_IMAGE}"
UBOOT_DTB_BINARY = "u-boot.dtb"
UBOOT_ENTRYPOINT  = "0x80000000"
UBOOT_LOADADDRESS = "0x80000000"
UBOOT_RD_LOADADDRESS = "0x88000000"
UBOOT_RD_ENTRYPOINT = "0x88000000"
UBOOT_DTB_LOADADDRESS = "0x82000000"
UBOOT_ARCH = "arm"
UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
UBOOT_MKIMAGE_KERNEL_TYPE = "kernel"
UBOOT_EXTLINUX = "0"
KERNEL_IMAGETYPE_REPLACEMENT = "zImage"
FIT_KERNEL_COMP_ALG = "none"
FIT_HASH_ALG = "sha256"
"""
        config = self._config_add_kernel_classes(config)
        config = self._config_add_uboot_env(config)
        self.write_config(config)

        # Retrieve some variables from bitbake
        bb_vars = self._fit_get_bb_vars([
            'FIT_KEY_GENRSA_ARGS',
            'FIT_KEY_REQ_ARGS',
            'FIT_KEY_SIGN_PKCS',
            'FIT_SIGN_NUMBITS',
            'UBOOT_SIGN_KEYDIR',
        ])

        self._gen_signing_key(bb_vars)
        self._test_fitimage(bb_vars)

    def test_fit_image_sign_initramfs_bundle(self):
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
DISTRO = "poky"
MACHINE = "beaglebone-yocto"
INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "core-image-minimal-initramfs"
INITRAMFS_SCRIPTS = ""
UBOOT_MACHINE = "am335x_evm_defconfig"
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
KERNEL_IMAGETYPE_REPLACEMENT = "zImage"
FIT_KERNEL_COMP_ALG = "none"
FIT_HASH_ALG = "sha256"
"""
        config = self._config_add_kernel_classes(config)
        config = self._config_add_uboot_env(config)
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()
        self._gen_signing_key(bb_vars)
        self._test_fitimage(bb_vars)

class FitImagePyTests(KernelFitImageBase):
    """Test cases for the fitimage.py module without calling bitbake"""

    def _test_fitimage_py(self, bb_vars_overrides=None):
        topdir = os.path.join(os.environ['BUILDDIR'])
        fitimage_its_path = os.path.join(topdir, self._testMethodName + '.its')

        # Provide variables without calling bitbake
        bb_vars = {
            # image-fitimage.conf
            'FIT_DESC': "Kernel fitImage for a dummy distro",
            'FIT_HASH_ALG': "sha256",
            'FIT_SIGN_ALG': "rsa2048",
            'FIT_PAD_ALG': "pkcs-1.5",
            'FIT_GENERATE_KEYS': "0",
            'FIT_SIGN_NUMBITS': "2048",
            'FIT_KEY_GENRSA_ARGS': "-F4",
            'FIT_KEY_REQ_ARGS': "-batch -new",
            'FIT_KEY_SIGN_PKCS': "-x509",
            'FIT_SIGN_INDIVIDUAL': "0",
            'FIT_CONF_PREFIX': "conf-",
            'FIT_SUPPORTED_INITRAMFS_FSTYPES': "cpio.lz4 cpio.lzo cpio.lzma cpio.xz cpio.zst cpio.gz ext2.gz cpio",
            'FIT_CONF_DEFAULT_DTB': "",
            'FIT_ADDRESS_CELLS': "1",
            'FIT_UBOOT_ENV': "",
            # kernel.bbclass
            'UBOOT_ENTRYPOINT': "0x20008000",
            'UBOOT_LOADADDRESS': "0x20008000",
            'INITRAMFS_IMAGE': "",
            'INITRAMFS_IMAGE_BUNDLE': "",
            # kernel-uboot.bbclass
            'FIT_KERNEL_COMP_ALG': "gzip",
            'FIT_KERNEL_COMP_ALG_EXTENSION': ".gz",
            'UBOOT_MKIMAGE_KERNEL_TYPE': "kernel",
            # uboot-config.bbclass
            'UBOOT_MKIMAGE_DTCOPTS': "",
            'UBOOT_MKIMAGE': "uboot-mkimage",
            'UBOOT_MKIMAGE_SIGN': "uboot-mkimage",
            'UBOOT_MKIMAGE_SIGN_ARGS': "",
            'UBOOT_SIGN_ENABLE': "0",
            'UBOOT_SIGN_KEYDIR': None,
            'UBOOT_SIGN_KEYNAME': None,
            'UBOOT_SIGN_IMG_KEYNAME': None,
            # others
            'MACHINE': "qemux86-64",
            'UBOOT_ARCH': "x86",
            'HOST_PREFIX': "x86_64-poky-linux-"
        }
        if bb_vars_overrides:
            bb_vars.update(bb_vars_overrides)

        root_node = oe.fitimage.ItsNodeRootKernel(
            bb_vars["FIT_DESC"], bb_vars["FIT_ADDRESS_CELLS"],
            bb_vars['HOST_PREFIX'], bb_vars['UBOOT_ARCH'],  bb_vars["FIT_CONF_PREFIX"],
            oe.types.boolean(bb_vars['UBOOT_SIGN_ENABLE']), bb_vars["UBOOT_SIGN_KEYDIR"],
            bb_vars["UBOOT_MKIMAGE"], bb_vars["UBOOT_MKIMAGE_DTCOPTS"],
            bb_vars["UBOOT_MKIMAGE_SIGN"], bb_vars["UBOOT_MKIMAGE_SIGN_ARGS"],
            bb_vars['FIT_HASH_ALG'], bb_vars['FIT_SIGN_ALG'], bb_vars['FIT_PAD_ALG'],
            bb_vars['UBOOT_SIGN_KEYNAME'],
            oe.types.boolean(bb_vars['FIT_SIGN_INDIVIDUAL']), bb_vars['UBOOT_SIGN_IMG_KEYNAME']
        )

        root_node.fitimage_emit_section_kernel("kernel-1", "linux.bin", "none",
            bb_vars.get('UBOOT_LOADADDRESS'), bb_vars.get('UBOOT_ENTRYPOINT'),
            bb_vars.get('UBOOT_MKIMAGE_KERNEL_TYPE'), bb_vars.get("UBOOT_ENTRYSYMBOL")
        )

        dtb_files, _ = FitImageTestCase._get_dtb_files(bb_vars)
        for dtb in dtb_files:
            root_node.fitimage_emit_section_dtb(dtb, os.path.join("a-dir", dtb),
                bb_vars.get("UBOOT_DTB_LOADADDRESS"), bb_vars.get("UBOOT_DTBO_LOADADDRESS"))

        if bb_vars.get('FIT_UBOOT_ENV'):
            root_node.fitimage_emit_section_boot_script(
                "bootscr-" + bb_vars['FIT_UBOOT_ENV'], bb_vars['FIT_UBOOT_ENV'])

        if bb_vars['MACHINE'] == "qemux86-64": # Not really the right if
            root_node.fitimage_emit_section_setup("setup-1", "setup1.bin")

        if bb_vars.get('INITRAMFS_IMAGE') and bb_vars.get("INITRAMFS_IMAGE_BUNDLE") != "1":
            root_node.fitimage_emit_section_ramdisk("ramdisk-1", "a-dir/a-initramfs-1",
                "core-image-minimal-initramfs",
                bb_vars.get("UBOOT_RD_LOADADDRESS"), bb_vars.get("UBOOT_RD_ENTRYPOINT"))

        root_node.fitimage_emit_section_config(bb_vars['FIT_CONF_DEFAULT_DTB'])
        root_node.write_its_file(fitimage_its_path)

        self.assertExists(fitimage_its_path, "%s image tree source doesn't exist" % (fitimage_its_path))
        self.logger.debug("Checking its: %s" % fitimage_its_path)
        self._check_its_file(bb_vars, fitimage_its_path)

    def test_fitimage_py_default(self):
        self._test_fitimage_py()

    def test_fitimage_py_default_dtb(self):
        bb_vars_overrides = {
            'KERNEL_DEVICETREE': "one.dtb two.dtb three.dtb",
            'FIT_CONF_DEFAULT_DTB': "two.dtb"
        }
        self._test_fitimage_py(bb_vars_overrides)


class UBootFitImageTests(FitImageTestCase):
    """Test cases for the uboot-sign bbclass"""

    BOOTLOADER_RECIPE = "virtual/bootloader"

    def _fit_get_bb_vars(self, additional_vars=[]):
        """Get bb_vars as needed by _test_sign_fit_image

        Call the get_bb_vars function once and get all variables needed by the test case.
        """
        internal_used = {
            'DEPLOY_DIR_IMAGE',
            'FIT_HASH_ALG',
            'FIT_KEY_GENRSA_ARGS',
            'FIT_KEY_REQ_ARGS',
            'FIT_KEY_SIGN_PKCS',
            'FIT_SIGN_ALG',
            'FIT_SIGN_INDIVIDUAL',
            'FIT_SIGN_NUMBITS',
            'MACHINE',
            'SPL_MKIMAGE_SIGN_ARGS',
            'SPL_SIGN_ENABLE',
            'SPL_SIGN_KEYNAME',
            'UBOOT_ARCH',
            'UBOOT_DTB_BINARY',
            'UBOOT_DTB_IMAGE',
            'UBOOT_FIT_ARM_TRUSTED_FIRMWARE_ENTRYPOINT',
            'UBOOT_FIT_ARM_TRUSTED_FIRMWARE_LOADADDRESS',
            'UBOOT_FIT_ARM_TRUSTED_FIRMWARE',
            'UBOOT_FIT_CONF_USER_LOADABLES',
            'UBOOT_FIT_DESC',
            'UBOOT_FIT_HASH_ALG',
            'UBOOT_FIT_SIGN_ALG',
            'UBOOT_FIT_TEE_ENTRYPOINT',
            'UBOOT_FIT_TEE_LOADADDRESS',
            'UBOOT_FIT_TEE',
            'UBOOT_FIT_UBOOT_ENTRYPOINT',
            'UBOOT_FIT_UBOOT_LOADADDRESS',
            'UBOOT_FIT_USER_SETTINGS',
            'UBOOT_FITIMAGE_ENABLE',
            'UBOOT_NODTB_BINARY',
            'UBOOT_SIGN_ENABLE',
            'UBOOT_SIGN_IMG_KEYNAME',
            'UBOOT_SIGN_KEYDIR',
            'UBOOT_SIGN_KEYNAME',
        }
        bb_vars = get_bb_vars(list(internal_used | set(additional_vars)), UBootFitImageTests.BOOTLOADER_RECIPE)
        self.logger.debug("bb_vars: %s" % pprint.pformat(bb_vars, indent=4))
        return bb_vars

    def _bitbake_fit_image(self, bb_vars):
        """Bitbake the bootloader and return the paths to the its file and the FIT image"""
        bitbake(UBootFitImageTests.BOOTLOADER_RECIPE)

        deploy_dir_image = bb_vars['DEPLOY_DIR_IMAGE']
        machine = bb_vars['MACHINE']
        fitimage_its_path = os.path.join(deploy_dir_image, "u-boot-its-%s" % machine)
        fitimage_path = os.path.join(deploy_dir_image, "u-boot-fitImage-%s" % machine)
        return (fitimage_its_path, fitimage_path)

    def _get_req_its_paths(self, bb_vars):
        # image nodes
        images = [ 'uboot', 'fdt',  ]
        if bb_vars['UBOOT_FIT_TEE'] == "1":
            images.append('tee')
        if bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE'] == "1":
            images.append('atf')
        # if bb_vars['UBOOT_FIT_USER_SETTINGS']:

        # configuration nodes
        configurations = [ 'conf']

        # Create a list of paths for all image and configuration nodes
        req_its_paths = []
        for image in images:
            req_its_paths.append(['/', 'images', image])
            if bb_vars['SPL_SIGN_ENABLE'] == "1":
                req_its_paths.append(['/', 'images', image, 'signature'])
        for configuration in configurations:
            req_its_paths.append(['/', 'configurations', configuration])
        return req_its_paths

    def _get_req_its_fields(self, bb_vars):
        loadables = ["uboot"]
        its_field_check = [
            'description = "%s";' % bb_vars['UBOOT_FIT_DESC'],
            'description = "U-Boot image";',
            'data = /incbin/("%s");' % bb_vars['UBOOT_NODTB_BINARY'],
            'type = "standalone";',
            'os = "u-boot";',
            'arch = "%s";' % bb_vars['UBOOT_ARCH'],
            'compression = "none";',
            'load = <%s>;' % bb_vars['UBOOT_FIT_UBOOT_LOADADDRESS'],
            'entry = <%s>;' % bb_vars['UBOOT_FIT_UBOOT_ENTRYPOINT'],
            'description = "U-Boot FDT";',
            'data = /incbin/("%s");' % bb_vars['UBOOT_DTB_BINARY'],
            'type = "flat_dt";',
            'arch = "%s";' % bb_vars['UBOOT_ARCH'],
            'compression = "none";',
        ]
        if bb_vars['UBOOT_FIT_TEE'] == "1":
            its_field_check += [
                'description = "Trusted Execution Environment";',
                'data = /incbin/("%s");' % bb_vars['UBOOT_FIT_TEE_IMAGE'],
                'type = "tee";',
                'arch = "%s";' % bb_vars['UBOOT_ARCH'],
                'os = "tee";',
                'load = <%s>;' % bb_vars['UBOOT_FIT_TEE_LOADADDRESS'],
                'entry = <%s>;' % bb_vars['UBOOT_FIT_TEE_ENTRYPOINT'],
                'compression = "none";',
            ]
            loadables.insert(0, "tee")
        if bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE'] == "1":
            its_field_check += [
                'description = "ARM Trusted Firmware";',
                'data = /incbin/("%s");' % bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE_IMAGE'],
                'type = "firmware";',
                'arch = "%s";' % bb_vars['UBOOT_ARCH'],
                'os = "arm-trusted-firmware";',
                'load = <%s>;' % bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE_LOADADDRESS'],
                'entry = <%s>;' % bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE_ENTRYPOINT'],
                'compression = "none";',
            ]
            loadables.insert(0, "atf")
        its_field_check += [
            'default = "conf";',
            'description = "Boot with signed U-Boot FIT";',
            'loadables = "%s";' % '", "'.join(loadables),
            'fdt = "fdt";',
        ]
        return its_field_check

    def _get_req_sigvalues_config(self, bb_vars):
        # COnfigurations are not signed by uboot-sign
        return {}

    def _get_req_sigvalues_image(self, bb_vars):
        if bb_vars['SPL_SIGN_ENABLE'] != "1":
            return {}
        req_sigvalues_image = {
            'algo': '"%s,%s"' % (bb_vars['UBOOT_FIT_HASH_ALG'], bb_vars['UBOOT_FIT_SIGN_ALG']),
            'key-name-hint': '"%s"' % bb_vars['SPL_SIGN_KEYNAME'],
        }
        return req_sigvalues_image

    def _get_req_sections(self, bb_vars):
        """Generate the expected output of dumpimage for beaglebone targets

        The dict generated by this function is supposed to be compared against
        the dict which is generated by the _dump_fitimage function.
        """
        loadables = ['uboot']
        req_sections = {
            "uboot": {
                "Type": "Standalone Program",
                "Load Address": bb_vars['UBOOT_FIT_UBOOT_LOADADDRESS'],
                "Entry Point": bb_vars['UBOOT_FIT_UBOOT_ENTRYPOINT'],
            },
            "fdt": {
                "Type": "Flat Device Tree",
            }
        }
        if bb_vars['UBOOT_FIT_TEE'] == "1":
            loadables.insert(0, "tee")
            req_sections['tee'] = {
                "Type": "Trusted Execution Environment Image",
                # "Load Address": bb_vars['UBOOT_FIT_TEE_LOADADDRESS'], not printed by mkimage?
                # "Entry Point": bb_vars['UBOOT_FIT_TEE_ENTRYPOINT'], not printed by mkimage?
            }
        if bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE'] == "1":
            loadables.insert(0, "atf")
            req_sections['atf'] = {
                "Type": "Firmware",
                "Load Address": bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE_LOADADDRESS'],
                # "Entry Point": bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE_ENTRYPOINT'], not printed by mkimage?
            }
        req_sections["conf"] = {
            "Kernel": "unavailable",
            "FDT": "fdt",
            "Loadables": ','.join(loadables),
        }

        # Add signing related properties if needed
        uboot_fit_hash_alg = bb_vars['UBOOT_FIT_HASH_ALG']
        uboot_fit_sign_alg = bb_vars['UBOOT_FIT_SIGN_ALG']
        spl_sign_enable = bb_vars['SPL_SIGN_ENABLE']
        spl_sign_keyname = bb_vars['SPL_SIGN_KEYNAME']
        num_signatures = 0
        if spl_sign_enable == "1":
            for section in req_sections:
                if not section.startswith('conf'):
                    req_sections[section]['Sign algo'] = "%s,%s:%s" % \
                        (uboot_fit_hash_alg, uboot_fit_sign_alg, spl_sign_keyname)
                    num_signatures += 1
        return (req_sections, num_signatures)

    def  _check_signing(self, bb_vars, sections, num_signatures, uboot_tools_bindir, fitimage_path):
        if bb_vars['UBOOT_FITIMAGE_ENABLE'] == '1' and bb_vars['SPL_SIGN_ENABLE'] == "1":
            self.logger.debug("Verifying signatures in the FIT image")
        else:
            self.logger.debug("FIT image is not signed. Signature verification is not needed.")
            return

        uboot_fit_hash_alg = bb_vars['UBOOT_FIT_HASH_ALG']
        uboot_fit_sign_alg = bb_vars['UBOOT_FIT_SIGN_ALG']
        spl_sign_keyname = bb_vars['SPL_SIGN_KEYNAME']
        fit_sign_alg_len = FitImageTestCase.MKIMAGE_SIGNATURE_LENGTHS[uboot_fit_sign_alg]
        for section, values in sections.items():
            # Configuration nodes are always signed with UBOOT_SIGN_KEYNAME (if UBOOT_SIGN_ENABLE = "1")
            if section.startswith("conf"):
                # uboot-sign does not sign configuration nodes
                pass
            else:
                # uboot-sign does not add hash nodes, only image signatures
                sign_algo = values.get('Sign algo', None)
                req_sign_algo = "%s,%s:%s" % (uboot_fit_hash_alg, uboot_fit_sign_alg, spl_sign_keyname)
                self.assertEqual(sign_algo, req_sign_algo, 'Signature algorithm for %s not expected value' % section)
                sign_value = values.get('Sign value', None)
                self.assertEqual(len(sign_value), fit_sign_alg_len, 'Signature value for section %s not expected length' % section)

        # Search for the string passed to mkimage in each signed section of the FIT image.
        # Looks like mkimage supports to add a comment but does not support to read it back.
        a_comment = FitImageTestCase._get_uboot_mkimage_sign_args(bb_vars['SPL_MKIMAGE_SIGN_ARGS'])
        self.logger.debug("a_comment: %s" % a_comment)
        if a_comment:
            found_comments = FitImageTestCase._find_string_in_bin_file(fitimage_path, a_comment)
            self.assertEqual(found_comments, num_signatures, "Expected %d signed and commented (%s) sections in the fitImage." %
                             (num_signatures, a_comment))

    def _check_kernel_dtb(self, bb_vars):
        """
        Check if the device-tree from U-Boot has the kernel public key(s).

        The concat_dtb function of the uboot-sign.bbclass injects the public keys
        which are required for verifying the kernel at run-time into the DTB from
        U-Boot. The following example is from a build with FIT_SIGN_INDIVIDUAL
        set to "1". If it is set to "0" the key-the-kernel-image-key node is not
        present.
        / {
            ...
            signature {
                key-the-kernel-image-key {
                required = "image";
                algo = "sha256,rsa2048";
                ...
            };
            key-the-kernel-config-key {
                required = "conf";
                algo = "sha256,rsa2048";
                ...
            };
        };
        """
        # Setup u-boot-tools-native
        dtc_bindir = FitImageTestCase._setup_native('dtc-native')

        # Check if 1 or 2 signature sections are in the DTB.
        uboot_dtb_path = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], bb_vars['UBOOT_DTB_IMAGE'])
        algo = "%s,%s" % (bb_vars['FIT_HASH_ALG'], bb_vars['FIT_SIGN_ALG'])
        if bb_vars['FIT_SIGN_INDIVIDUAL'] == "1":
            uboot_sign_img_keyname = bb_vars['UBOOT_SIGN_IMG_KEYNAME']
            key_dtb_path = "/signature/key-" + uboot_sign_img_keyname
            self._verify_dtb_property(dtc_bindir, uboot_dtb_path, key_dtb_path, "required", "image")
            self._verify_dtb_property(dtc_bindir, uboot_dtb_path, key_dtb_path, "algo", algo)
            self._verify_dtb_property(dtc_bindir, uboot_dtb_path, key_dtb_path, "key-name-hint", uboot_sign_img_keyname)

        uboot_sign_keyname = bb_vars['UBOOT_SIGN_KEYNAME']
        key_dtb_path = "/signature/key-" + uboot_sign_keyname
        self._verify_dtb_property(dtc_bindir, uboot_dtb_path, key_dtb_path, "required", "conf")
        self._verify_dtb_property(dtc_bindir, uboot_dtb_path, key_dtb_path, "algo", algo)
        self._verify_dtb_property(dtc_bindir, uboot_dtb_path, key_dtb_path, "key-name-hint", uboot_sign_keyname)


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
"""
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()
        self._test_fitimage(bb_vars)


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
        config = """
# There's no U-boot defconfig with CONFIG_FIT_SIGNATURE yet, so we need at
# least CONFIG_SPL_LOAD_FIT and CONFIG_SPL_OF_CONTROL set
MACHINE = "qemuarm"
UBOOT_MACHINE = "am57xx_evm_defconfig"
SPL_BINARY = "MLO"
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
SPL_MKIMAGE_SIGN_ARGS = "-c 'a smart U-Boot comment'"
UBOOT_EXTLINUX = "0"
UBOOT_FIT_GENERATE_KEYS = "1"
UBOOT_FIT_HASH_ALG = "sha256"
"""
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()
        self._test_fitimage(bb_vars)


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
                     4) Examination of the do_uboot_assemble_fitimage that
                     UBOOT_MKIMAGE, UBOOT_MKIMAGE_SIGN and SPL_MKIMAGE_SIGN_ARGS
                     are working as expected.
        Product:     oe-core
        Author:      Klaus Heinrich Kiwi <klaus@linux.vnet.ibm.com> based upon
                     work by Paul Eggleton <paul.eggleton@microsoft.com> and
                     Usama Arif <usama.arif@arm.com>
        """
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
UBOOT_MKIMAGE_SIGN_ARGS = "-c 'a smart cascaded U-Boot comment'"
UBOOT_DTB_LOADADDRESS = "0x82000000"
UBOOT_ARCH = "arm"
SPL_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
SPL_MKIMAGE_SIGN_ARGS = "-c 'a smart cascaded U-Boot comment'"
UBOOT_EXTLINUX = "0"
UBOOT_FIT_GENERATE_KEYS = "1"
UBOOT_FIT_HASH_ALG = "sha256"
UBOOT_SIGN_ENABLE = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_KEYNAME = "cfg-oe-selftest"
"""
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()

        self._gen_signing_key(bb_vars)
        self._test_fitimage(bb_vars)
        self._check_kernel_dtb(bb_vars)

    def test_uboot_atf_tee_fit_image(self):
        """
        Summary:     Check if U-boot FIT image and Image Tree Source
                     (its) are built and the Image Tree Source has the
                     correct fields.
        Expected:    1. Create atf and tee dummy images
                     2. Both u-boot-fitImage and u-boot-its can be built
                     3. The os, load address, entrypoint address and
                        default values of U-boot, ATF and TEE images are
                        correct in the Image Tree Source. Not all the
                        fields are tested, only the key fields that wont
                        vary between different architectures.
                Product:     oe-core
                Author:      Jamin Lin <jamin_lin@aspeedtech.com>
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

# Enable creation of the TEE fitImage
UBOOT_FIT_TEE = "1"

# TEE fitImage properties
UBOOT_FIT_TEE_IMAGE = "${TOPDIR}/tee-dummy.bin"
UBOOT_FIT_TEE_LOADADDRESS = "0x80180000"
UBOOT_FIT_TEE_ENTRYPOINT = "0x80180000"

# Enable creation of the ATF fitImage
UBOOT_FIT_ARM_TRUSTED_FIRMWARE = "1"

# ATF fitImage properties
UBOOT_FIT_ARM_TRUSTED_FIRMWARE_IMAGE = "${TOPDIR}/atf-dummy.bin"
UBOOT_FIT_ARM_TRUSTED_FIRMWARE_LOADADDRESS = "0x80280000"
UBOOT_FIT_ARM_TRUSTED_FIRMWARE_ENTRYPOINT = "0x80280000"
"""
        self.write_config(config)

        bb_vars = self._fit_get_bb_vars([
            'UBOOT_FIT_ARM_TRUSTED_FIRMWARE_IMAGE',
            'UBOOT_FIT_TEE_IMAGE',
        ])

        # Create an ATF dummy image
        dummy_atf = os.path.join(self.builddir, bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE_IMAGE'])
        FitImageTestCase._gen_random_file(dummy_atf)

        # Create a TEE dummy image
        dummy_tee = os.path.join(self.builddir, bb_vars['UBOOT_FIT_TEE_IMAGE'])
        FitImageTestCase._gen_random_file(dummy_tee)

        self._test_fitimage(bb_vars)

    def test_sign_standalone_uboot_atf_tee_fit_image(self):
        """
        Summary:     Check if U-Boot FIT image and Image Tree Source (its) are
                     created and signed correctly for the scenario where only
                     the U-Boot proper fitImage is being created and signed.
        Expected:    1. Create atf and tee dummy images
                     2. U-Boot its and FIT image are built successfully
                     3. Scanning the its file indicates signing is enabled
                        as requested by SPL_SIGN_ENABLE (using keys generated
                        via UBOOT_FIT_GENERATE_KEYS)
                     4. Dumping the FIT image indicates signature values
                        are present
                     5. Examination of the do_uboot_assemble_fitimage
                     runfile/logfile indicate that UBOOT_MKIMAGE, UBOOT_MKIMAGE_SIGN
                     and SPL_MKIMAGE_SIGN_ARGS are working as expected.
                Product:     oe-core
                Author:      Jamin Lin <jamin_lin@aspeedtech.com>
        """
        config = """
# There's no U-boot deconfig with CONFIG_FIT_SIGNATURE yet, so we need at
# least CONFIG_SPL_LOAD_FIT and CONFIG_SPL_OF_CONTROL set
MACHINE = "qemuarm"
UBOOT_MACHINE = "am57xx_evm_defconfig"
SPL_BINARY = "MLO"
# Enable creation and signing of the U-Boot fitImage
UBOOT_FITIMAGE_ENABLE = "1"
SPL_SIGN_ENABLE = "1"
SPL_SIGN_KEYNAME = "spl-oe-selftest"
SPL_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_DTB_BINARY = "u-boot.dtb"
UBOOT_ENTRYPOINT  = "0x80000000"
UBOOT_LOADADDRESS = "0x80000000"
UBOOT_ARCH = "arm"
SPL_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
SPL_MKIMAGE_SIGN_ARGS = "-c 'a smart U-Boot ATF TEE comment'"
UBOOT_EXTLINUX = "0"
UBOOT_FIT_GENERATE_KEYS = "1"
UBOOT_FIT_HASH_ALG = "sha256"

# Enable creation of the TEE fitImage
UBOOT_FIT_TEE = "1"

# TEE fitImage properties
UBOOT_FIT_TEE_IMAGE = "${TOPDIR}/tee-dummy.bin"
UBOOT_FIT_TEE_LOADADDRESS = "0x80180000"
UBOOT_FIT_TEE_ENTRYPOINT = "0x80180000"

# Enable creation of the ATF fitImage
UBOOT_FIT_ARM_TRUSTED_FIRMWARE = "1"

# ATF fitImage properties
UBOOT_FIT_ARM_TRUSTED_FIRMWARE_IMAGE = "${TOPDIR}/atf-dummy.bin"
UBOOT_FIT_ARM_TRUSTED_FIRMWARE_LOADADDRESS = "0x80280000"
UBOOT_FIT_ARM_TRUSTED_FIRMWARE_ENTRYPOINT = "0x80280000"
"""
        self.write_config(config)

        bb_vars = self._fit_get_bb_vars([
            'UBOOT_FIT_ARM_TRUSTED_FIRMWARE_IMAGE',
            'UBOOT_FIT_TEE_IMAGE',
        ])

        # Create an ATF dummy image
        dummy_atf = os.path.join(self.builddir, bb_vars['UBOOT_FIT_ARM_TRUSTED_FIRMWARE_IMAGE'])
        FitImageTestCase._gen_random_file(dummy_atf)

        # Create a TEE dummy image
        dummy_tee = os.path.join(self.builddir, bb_vars['UBOOT_FIT_TEE_IMAGE'])
        FitImageTestCase._gen_random_file(dummy_tee)

        self._test_fitimage(bb_vars)


    def test_sign_uboot_kernel_individual(self):
        """
        Summary:     Check if the device-tree from U-Boot has two public keys
                     for verifying the kernel FIT image created by the
                     kernel-fitimage.bbclass included.
                     This test sets: FIT_SIGN_INDIVIDUAL = "1"
        Expected:    There must be two signature nodes. One is required for
                     the individual image nodes, the other is required for the
                     verification of the configuration section.
        """
        config = """
# Enable creation of fitImage
MACHINE = "beaglebone-yocto"
UBOOT_SIGN_ENABLE = "1"
UBOOT_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_SIGN_KEYNAME = "the-kernel-config-key"
UBOOT_SIGN_IMG_KEYNAME = "the-kernel-image-key"
UBOOT_MKIMAGE_DTCOPTS="-I dts -O dtb -p 2000"
FIT_SIGN_INDIVIDUAL = "1"
"""
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()
        self._gen_signing_key(bb_vars)

        bitbake(UBootFitImageTests.BOOTLOADER_RECIPE)

        # Just check the DTB of u-boot since there is no u-boot FIT image
        self._check_kernel_dtb(bb_vars)


    def test_sign_uboot_fit_image_without_spl(self):
        """
        Summary:     Check if U-Boot FIT image and Image Tree Source (its) are
                     created and signed correctly for the scenario where only
                     the U-Boot proper fitImage is being created and signed
                     (no SPL included).
        Expected:    1) U-Boot its and FIT image are built successfully
                     2) Scanning the its file indicates signing is enabled
                        as requested by SPL_SIGN_ENABLE (using keys generated
                        via UBOOT_FIT_GENERATE_KEYS)
                     3) Dumping the FIT image indicates signature values
                        are present
                     4) Examination of the do_uboot_assemble_fitimage
                     runfile/logfile indicate that UBOOT_MKIMAGE and
                     UBOOT_MKIMAGE_SIGN are working as expected.
        Product:     oe-core
        Author:      Jamin Lin <jamin_lin@aspeedtech.com>
        """
        config = """
# There's no U-boot defconfig with CONFIG_FIT_SIGNATURE yet, so we need at
# least CONFIG_SPL_LOAD_FIT and CONFIG_SPL_OF_CONTROL set
MACHINE = "qemuarm"
UBOOT_MACHINE = "am57xx_evm_defconfig"
# Enable creation and signing of the U-Boot fitImage (no SPL)
UBOOT_FITIMAGE_ENABLE = "1"
SPL_DTB_BINARY = ""
SPL_SIGN_ENABLE = "1"
SPL_SIGN_KEYNAME = "spl-oe-selftest"
SPL_SIGN_KEYDIR = "${TOPDIR}/signing-keys"
UBOOT_FIT_GENERATE_KEYS = "1"
"""
        self.write_config(config)
        bb_vars = self._fit_get_bb_vars()
        self._test_fitimage(bb_vars)

