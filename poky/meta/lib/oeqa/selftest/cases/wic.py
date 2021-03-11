#
# Copyright (c) 2015, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# AUTHORS
# Ed Bartosh <ed.bartosh@linux.intel.com>

"""Test cases for wic."""

import os
import sys
import unittest

from glob import glob
from shutil import rmtree, copy
from functools import wraps, lru_cache
from tempfile import NamedTemporaryFile

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars, runqemu


@lru_cache(maxsize=32)
def get_host_arch(recipe):
    """A cached call to get_bb_var('HOST_ARCH', <recipe>)"""
    return get_bb_var('HOST_ARCH', recipe)


def only_for_arch(archs, image='core-image-minimal'):
    """Decorator for wrapping test cases that can be run only for specific target
    architectures. A list of compatible architectures is passed in `archs`.
    Current architecture will be determined by parsing bitbake output for
    `image` recipe.
    """
    def wrapper(func):
        @wraps(func)
        def wrapped_f(*args, **kwargs):
            arch = get_host_arch(image)
            if archs and arch not in archs:
                raise unittest.SkipTest("Testcase arch dependency not met: %s" % arch)
            return func(*args, **kwargs)
        wrapped_f.__name__ = func.__name__
        return wrapped_f
    return wrapper

def extract_files(debugfs_output):
    """
    extract file names from the output of debugfs -R 'ls -p',
    which looks like this:

     /2/040755/0/0/.//\n
     /2/040755/0/0/..//\n
     /11/040700/0/0/lost+found^M//\n
     /12/040755/1002/1002/run//\n
     /13/040755/1002/1002/sys//\n
     /14/040755/1002/1002/bin//\n
     /80/040755/1002/1002/var//\n
     /92/040755/1002/1002/tmp//\n
    """
    # NOTE the occasional ^M in file names
    return [line.split('/')[5].strip() for line in \
            debugfs_output.strip().split('/\n')]

def files_own_by_root(debugfs_output):
    for line in debugfs_output.strip().split('/\n'):
        if line.split('/')[3:5] != ['0', '0']:
            print(debugfs_output)
            return False
    return True

class WicTestCase(OESelftestTestCase):
    """Wic test class."""

    image_is_ready = False
    wicenv_cache = {}

    def setUpLocal(self):
        """This code is executed before each test method."""
        self.resultdir = self.builddir + "/wic-tmp/"
        super(WicTestCase, self).setUpLocal()

        # Do this here instead of in setUpClass as the base setUp does some
        # clean up which can result in the native tools built earlier in
        # setUpClass being unavailable.
        if not WicTestCase.image_is_ready:
            if get_bb_var('USE_NLS') == 'yes':
                bitbake('wic-tools')
            else:
                self.skipTest('wic-tools cannot be built due its (intltool|gettext)-native dependency and NLS disable')

            bitbake('core-image-minimal')
            bitbake('core-image-minimal-mtdutils')
            WicTestCase.image_is_ready = True

        rmtree(self.resultdir, ignore_errors=True)

    def tearDownLocal(self):
        """Remove resultdir as it may contain images."""
        rmtree(self.resultdir, ignore_errors=True)
        super(WicTestCase, self).tearDownLocal()

    def _get_image_env_path(self, image):
        """Generate and obtain the path to <image>.env"""
        if image not in WicTestCase.wicenv_cache:
            self.assertEqual(0, bitbake('%s -c do_rootfs_wicenv' % image).status)
            bb_vars = get_bb_vars(['STAGING_DIR', 'MACHINE'], image)
            stdir = bb_vars['STAGING_DIR']
            machine = bb_vars['MACHINE']
            WicTestCase.wicenv_cache[image] = os.path.join(stdir, machine, 'imgdata')
        return WicTestCase.wicenv_cache[image]

class Wic(WicTestCase):

    def test_version(self):
        """Test wic --version"""
        runCmd('wic --version')

    def test_help(self):
        """Test wic --help and wic -h"""
        runCmd('wic --help')
        runCmd('wic -h')

    def test_createhelp(self):
        """Test wic create --help"""
        runCmd('wic create --help')

    def test_listhelp(self):
        """Test wic list --help"""
        runCmd('wic list --help')

    def test_help_create(self):
        """Test wic help create"""
        runCmd('wic help create')

    def test_help_list(self):
        """Test wic help list"""
        runCmd('wic help list')

    def test_help_overview(self):
        """Test wic help overview"""
        runCmd('wic help overview')

    def test_help_plugins(self):
        """Test wic help plugins"""
        runCmd('wic help plugins')

    def test_help_kickstart(self):
        """Test wic help kickstart"""
        runCmd('wic help kickstart')

    def test_list_images(self):
        """Test wic list images"""
        runCmd('wic list images')

    def test_list_source_plugins(self):
        """Test wic list source-plugins"""
        runCmd('wic list source-plugins')

    def test_listed_images_help(self):
        """Test wic listed images help"""
        output = runCmd('wic list images').output
        imagelist = [line.split()[0] for line in output.splitlines()]
        for image in imagelist:
            runCmd('wic list %s help' % image)

    def test_unsupported_subcommand(self):
        """Test unsupported subcommand"""
        self.assertNotEqual(0, runCmd('wic unsupported', ignore_status=True).status)

    def test_no_command(self):
        """Test wic without command"""
        self.assertEqual(1, runCmd('wic', ignore_status=True).status)

    def test_build_image_name(self):
        """Test wic create wictestdisk --image-name=core-image-minimal"""
        cmd = "wic create wictestdisk --image-name=core-image-minimal -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_gpt_image(self):
        """Test creation of core-image-minimal with gpt table and UUID boot"""
        cmd = "wic create directdisk-gpt --image-name core-image-minimal -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-*.direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_iso_image(self):
        """Test creation of hybrid iso image with legacy and EFI boot"""
        config = 'INITRAMFS_IMAGE = "core-image-minimal-initramfs"\n'\
                 'MACHINE_FEATURES_append = " efi"\n'\
                 'DEPENDS_pn-core-image-minimal += "syslinux"\n'
        self.append_config(config)
        bitbake('core-image-minimal core-image-minimal-initramfs')
        self.remove_config(config)
        cmd = "wic create mkhybridiso --image-name core-image-minimal -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "HYBRID_ISO_IMG-*.direct")))
        self.assertEqual(1, len(glob(self.resultdir + "HYBRID_ISO_IMG-*.iso")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_qemux86_directdisk(self):
        """Test creation of qemux-86-directdisk image"""
        cmd = "wic create qemux86-directdisk -e core-image-minimal -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "qemux86-directdisk-*direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_mkefidisk(self):
        """Test creation of mkefidisk image"""
        cmd = "wic create mkefidisk -e core-image-minimal -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "mkefidisk-*direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_bootloader_config(self):
        """Test creation of directdisk-bootloader-config image"""
        config = 'DEPENDS_pn-core-image-minimal += "syslinux"\n'
        self.append_config(config)
        bitbake('core-image-minimal')
        self.remove_config(config)
        cmd = "wic create directdisk-bootloader-config -e core-image-minimal -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-bootloader-config-*direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_systemd_bootdisk(self):
        """Test creation of systemd-bootdisk image"""
        config = 'MACHINE_FEATURES_append = " efi"\n'
        self.append_config(config)
        bitbake('core-image-minimal')
        self.remove_config(config)
        cmd = "wic create systemd-bootdisk -e core-image-minimal -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "systemd-bootdisk-*direct")))

    def test_sdimage_bootpart(self):
        """Test creation of sdimage-bootpart image"""
        cmd = "wic create sdimage-bootpart -e core-image-minimal -o %s" % self.resultdir
        kimgtype = get_bb_var('KERNEL_IMAGETYPE', 'core-image-minimal')
        self.write_config('IMAGE_BOOT_FILES = "%s"\n' % kimgtype)
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "sdimage-bootpart-*direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_default_output_dir(self):
        """Test default output location"""
        for fname in glob("directdisk-*.direct"):
            os.remove(fname)
        config = 'DEPENDS_pn-core-image-minimal += "syslinux"\n'
        self.append_config(config)
        bitbake('core-image-minimal')
        self.remove_config(config)
        cmd = "wic create directdisk -e core-image-minimal"
        runCmd(cmd)
        self.assertEqual(1, len(glob("directdisk-*.direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_build_artifacts(self):
        """Test wic create directdisk providing all artifacts."""
        bb_vars = get_bb_vars(['STAGING_DATADIR', 'RECIPE_SYSROOT_NATIVE'],
                              'wic-tools')
        bb_vars.update(get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_ROOTFS'],
                                   'core-image-minimal'))
        bbvars = {key.lower(): value for key, value in bb_vars.items()}
        bbvars['resultdir'] = self.resultdir
        runCmd("wic create directdisk "
                        "-b %(staging_datadir)s "
                        "-k %(deploy_dir_image)s "
                        "-n %(recipe_sysroot_native)s "
                        "-r %(image_rootfs)s "
                        "-o %(resultdir)s" % bbvars)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-*.direct")))

    def test_compress_gzip(self):
        """Test compressing an image with gzip"""
        runCmd("wic create wictestdisk "
                                   "--image-name core-image-minimal "
                                   "-c gzip -o %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct.gz")))

    def test_compress_bzip2(self):
        """Test compressing an image with bzip2"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-c bzip2 -o %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct.bz2")))

    def test_compress_xz(self):
        """Test compressing an image with xz"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "--compress-with=xz -o %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct.xz")))

    def test_wrong_compressor(self):
        """Test how wic breaks if wrong compressor is provided"""
        self.assertEqual(2, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-c wrong -o %s" % self.resultdir,
                                   ignore_status=True).status)

    def test_debug_short(self):
        """Test -D option"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    def test_debug_long(self):
        """Test --debug option"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "--debug -o %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    def test_skip_build_check_short(self):
        """Test -s option"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-s -o %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    def test_skip_build_check_long(self):
        """Test --skip-build-check option"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "--skip-build-check "
                                   "--outdir %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    def test_build_rootfs_short(self):
        """Test -f option"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-f -o %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    def test_build_rootfs_long(self):
        """Test --build-rootfs option"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "--build-rootfs "
                                   "--outdir %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_rootfs_indirect_recipes(self):
        """Test usage of rootfs plugin with rootfs recipes"""
        runCmd("wic create directdisk-multi-rootfs "
                        "--image-name=core-image-minimal "
                        "--rootfs rootfs1=core-image-minimal "
                        "--rootfs rootfs2=core-image-minimal "
                        "--outdir %s" % self.resultdir)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-multi-rootfs*.direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_rootfs_artifacts(self):
        """Test usage of rootfs plugin with rootfs paths"""
        bb_vars = get_bb_vars(['STAGING_DATADIR', 'RECIPE_SYSROOT_NATIVE'],
                              'wic-tools')
        bb_vars.update(get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_ROOTFS'],
                                   'core-image-minimal'))
        bbvars = {key.lower(): value for key, value in bb_vars.items()}
        bbvars['wks'] = "directdisk-multi-rootfs"
        bbvars['resultdir'] = self.resultdir
        runCmd("wic create %(wks)s "
                        "--bootimg-dir=%(staging_datadir)s "
                        "--kernel-dir=%(deploy_dir_image)s "
                        "--native-sysroot=%(recipe_sysroot_native)s "
                        "--rootfs-dir rootfs1=%(image_rootfs)s "
                        "--rootfs-dir rootfs2=%(image_rootfs)s "
                        "--outdir %(resultdir)s" % bbvars)
        self.assertEqual(1, len(glob(self.resultdir + "%(wks)s-*.direct" % bbvars)))

    def test_exclude_path(self):
        """Test --exclude-path wks option."""

        oldpath = os.environ['PATH']
        os.environ['PATH'] = get_bb_var("PATH", "wic-tools")

        try:
            wks_file = 'temp.wks'
            with open(wks_file, 'w') as wks:
                rootfs_dir = get_bb_var('IMAGE_ROOTFS', 'core-image-minimal')
                wks.write("""
part / --source rootfs --ondisk mmcblk0 --fstype=ext4 --exclude-path usr
part /usr --source rootfs --ondisk mmcblk0 --fstype=ext4 --rootfs-dir %s/usr
part /etc --source rootfs --ondisk mmcblk0 --fstype=ext4 --exclude-path bin/ --rootfs-dir %s/usr"""
                          % (rootfs_dir, rootfs_dir))
            runCmd("wic create %s -e core-image-minimal -o %s" \
                                       % (wks_file, self.resultdir))

            os.remove(wks_file)
            wicout = glob(self.resultdir + "%s-*direct" % 'temp')
            self.assertEqual(1, len(wicout))

            wicimg = wicout[0]

            # verify partition size with wic
            res = runCmd("parted -m %s unit b p 2>/dev/null" % wicimg)

            # parse parted output which looks like this:
            # BYT;\n
            # /var/tmp/wic/build/tmpfwvjjkf_-201611101222-hda.direct:200MiB:file:512:512:msdos::;\n
            # 1:0.00MiB:200MiB:200MiB:ext4::;\n
            partlns = res.output.splitlines()[2:]

            self.assertEqual(3, len(partlns))

            for part in [1, 2, 3]:
                part_file = os.path.join(self.resultdir, "selftest_img.part%d" % part)
                partln = partlns[part-1].split(":")
                self.assertEqual(7, len(partln))
                start = int(partln[1].rstrip("B")) / 512
                length = int(partln[3].rstrip("B")) / 512
                runCmd("dd if=%s of=%s skip=%d count=%d" %
                                           (wicimg, part_file, start, length))

            # Test partition 1, should contain the normal root directories, except
            # /usr.
            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % \
                             os.path.join(self.resultdir, "selftest_img.part1"))
            files = extract_files(res.output)
            self.assertIn("etc", files)
            self.assertNotIn("usr", files)

            # Partition 2, should contain common directories for /usr, not root
            # directories.
            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % \
                             os.path.join(self.resultdir, "selftest_img.part2"))
            files = extract_files(res.output)
            self.assertNotIn("etc", files)
            self.assertNotIn("usr", files)
            self.assertIn("share", files)

            # Partition 3, should contain the same as partition 2, including the bin
            # directory, but not the files inside it.
            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % \
                             os.path.join(self.resultdir, "selftest_img.part3"))
            files = extract_files(res.output)
            self.assertNotIn("etc", files)
            self.assertNotIn("usr", files)
            self.assertIn("share", files)
            self.assertIn("bin", files)
            res = runCmd("debugfs -R 'ls -p bin' %s 2>/dev/null" % \
                             os.path.join(self.resultdir, "selftest_img.part3"))
            files = extract_files(res.output)
            self.assertIn(".", files)
            self.assertIn("..", files)
            self.assertEqual(2, len(files))

            for part in [1, 2, 3]:
                part_file = os.path.join(self.resultdir, "selftest_img.part%d" % part)
                os.remove(part_file)

        finally:
            os.environ['PATH'] = oldpath

    def test_include_path(self):
        """Test --include-path wks option."""

        oldpath = os.environ['PATH']
        os.environ['PATH'] = get_bb_var("PATH", "wic-tools")

        try:
            include_path = os.path.join(self.resultdir, 'test-include')
            os.makedirs(include_path)
            with open(os.path.join(include_path, 'test-file'), 'w') as t:
                t.write("test\n")
            wks_file = os.path.join(include_path, 'temp.wks')
            with open(wks_file, 'w') as wks:
                rootfs_dir = get_bb_var('IMAGE_ROOTFS', 'core-image-minimal')
                wks.write("""
part /part1 --source rootfs --ondisk mmcblk0 --fstype=ext4
part /part2 --source rootfs --ondisk mmcblk0 --fstype=ext4 --include-path %s"""
                          % (include_path))
            runCmd("wic create %s -e core-image-minimal -o %s" \
                                       % (wks_file, self.resultdir))

            part1 = glob(os.path.join(self.resultdir, 'temp-*.direct.p1'))[0]
            part2 = glob(os.path.join(self.resultdir, 'temp-*.direct.p2'))[0]

            # Test partition 1, should not contain 'test-file'
            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % (part1))
            files = extract_files(res.output)
            self.assertNotIn('test-file', files)

            # Test partition 2, should not contain 'test-file'
            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % (part2))
            files = extract_files(res.output)
            self.assertIn('test-file', files)

        finally:
            os.environ['PATH'] = oldpath

    def test_exclude_path_errors(self):
        """Test --exclude-path wks option error handling."""
        wks_file = 'temp.wks'

        # Absolute argument.
        with open(wks_file, 'w') as wks:
            wks.write("part / --source rootfs --ondisk mmcblk0 --fstype=ext4 --exclude-path /usr")
        self.assertNotEqual(0, runCmd("wic create %s -e core-image-minimal -o %s" \
                                      % (wks_file, self.resultdir), ignore_status=True).status)
        os.remove(wks_file)

        # Argument pointing to parent directory.
        with open(wks_file, 'w') as wks:
            wks.write("part / --source rootfs --ondisk mmcblk0 --fstype=ext4 --exclude-path ././..")
        self.assertNotEqual(0, runCmd("wic create %s -e core-image-minimal -o %s" \
                                      % (wks_file, self.resultdir), ignore_status=True).status)
        os.remove(wks_file)

    def test_permissions(self):
        """Test permissions are respected"""

        # prepare wicenv and rootfs
        bitbake('core-image-minimal core-image-minimal-mtdutils -c do_rootfs_wicenv')

        oldpath = os.environ['PATH']
        os.environ['PATH'] = get_bb_var("PATH", "wic-tools")

        t_normal = """
part / --source rootfs --fstype=ext4
"""
        t_exclude = """
part / --source rootfs --fstype=ext4 --exclude-path=home
"""
        t_multi = """
part / --source rootfs --ondisk sda --fstype=ext4
part /export --source rootfs --rootfs=core-image-minimal-mtdutils --fstype=ext4
"""
        t_change = """
part / --source rootfs --ondisk sda --fstype=ext4 --exclude-path=etc/   
part /etc --source rootfs --fstype=ext4 --change-directory=etc
"""
        tests = [t_normal, t_exclude, t_multi, t_change]

        try:
            for test in tests:
                include_path = os.path.join(self.resultdir, 'test-include')
                os.makedirs(include_path)
                wks_file = os.path.join(include_path, 'temp.wks')
                with open(wks_file, 'w') as wks:
                    wks.write(test)
                runCmd("wic create %s -e core-image-minimal -o %s" \
                                       % (wks_file, self.resultdir))

                for part in glob(os.path.join(self.resultdir, 'temp-*.direct.p*')):
                    res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % (part))
                    self.assertEqual(True, files_own_by_root(res.output))

                config = 'IMAGE_FSTYPES += "wic"\nWKS_FILE = "%s"\n' % wks_file
                self.append_config(config)
                bitbake('core-image-minimal')
                tmpdir = os.path.join(get_bb_var('WORKDIR', 'core-image-minimal'),'build-wic')

                # check each partition for permission
                for part in glob(os.path.join(tmpdir, 'temp-*.direct.p*')):
                    res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % (part))
                    self.assertTrue(files_own_by_root(res.output)
                        ,msg='Files permission incorrect using wks set "%s"' % test)

                # clean config and result directory for next cases
                self.remove_config(config)
                rmtree(self.resultdir, ignore_errors=True)

        finally:
            os.environ['PATH'] = oldpath

    def test_change_directory(self):
        """Test --change-directory wks option."""

        oldpath = os.environ['PATH']
        os.environ['PATH'] = get_bb_var("PATH", "wic-tools")

        try:
            include_path = os.path.join(self.resultdir, 'test-include')
            os.makedirs(include_path)
            wks_file = os.path.join(include_path, 'temp.wks')
            with open(wks_file, 'w') as wks:
                wks.write("part /etc --source rootfs --fstype=ext4 --change-directory=etc")
            runCmd("wic create %s -e core-image-minimal -o %s" \
                                       % (wks_file, self.resultdir))

            part1 = glob(os.path.join(self.resultdir, 'temp-*.direct.p1'))[0]

            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % (part1))
            files = extract_files(res.output)
            self.assertIn('passwd', files)

        finally:
            os.environ['PATH'] = oldpath

    def test_change_directory_errors(self):
        """Test --change-directory wks option error handling."""
        wks_file = 'temp.wks'

        # Absolute argument.
        with open(wks_file, 'w') as wks:
            wks.write("part / --source rootfs --fstype=ext4 --change-directory /usr")
        self.assertNotEqual(0, runCmd("wic create %s -e core-image-minimal -o %s" \
                                      % (wks_file, self.resultdir), ignore_status=True).status)
        os.remove(wks_file)

        # Argument pointing to parent directory.
        with open(wks_file, 'w') as wks:
            wks.write("part / --source rootfs --fstype=ext4 --change-directory ././..")
        self.assertNotEqual(0, runCmd("wic create %s -e core-image-minimal -o %s" \
                                      % (wks_file, self.resultdir), ignore_status=True).status)
        os.remove(wks_file)

class Wic2(WicTestCase):

    def test_bmap_short(self):
        """Test generation of .bmap file -m option"""
        cmd = "wic create wictestdisk -e core-image-minimal -m -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct")))
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct.bmap")))

    def test_bmap_long(self):
        """Test generation of .bmap file --bmap option"""
        cmd = "wic create wictestdisk -e core-image-minimal --bmap -o %s" % self.resultdir
        runCmd(cmd)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct")))
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct.bmap")))

    def test_image_env(self):
        """Test generation of <image>.env files."""
        image = 'core-image-minimal'
        imgdatadir = self._get_image_env_path(image)

        bb_vars = get_bb_vars(['IMAGE_BASENAME', 'WICVARS'], image)
        basename = bb_vars['IMAGE_BASENAME']
        self.assertEqual(basename, image)
        path = os.path.join(imgdatadir, basename) + '.env'
        self.assertTrue(os.path.isfile(path))

        wicvars = set(bb_vars['WICVARS'].split())
        # filter out optional variables
        wicvars = wicvars.difference(('DEPLOY_DIR_IMAGE', 'IMAGE_BOOT_FILES',
                                      'INITRD', 'INITRD_LIVE', 'ISODIR','INITRAMFS_IMAGE',
                                      'INITRAMFS_IMAGE_BUNDLE', 'INITRAMFS_LINK_NAME',
                                      'APPEND'))
        with open(path) as envfile:
            content = dict(line.split("=", 1) for line in envfile)
            # test if variables used by wic present in the .env file
            for var in wicvars:
                self.assertTrue(var in content, "%s is not in .env file" % var)
                self.assertTrue(content[var])

    def test_image_vars_dir_short(self):
        """Test image vars directory selection -v option"""
        image = 'core-image-minimal'
        imgenvdir = self._get_image_env_path(image)
        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "wic-tools")

        runCmd("wic create wictestdisk "
                                   "--image-name=%s -v %s -n %s -o %s"
                                   % (image, imgenvdir, native_sysroot,
                                      self.resultdir))
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct")))

    def test_image_vars_dir_long(self):
        """Test image vars directory selection --vars option"""
        image = 'core-image-minimal'
        imgenvdir = self._get_image_env_path(image)
        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "wic-tools")

        runCmd("wic create wictestdisk "
                                   "--image-name=%s "
                                   "--vars %s "
                                   "--native-sysroot %s "
                                   "--outdir %s"
                                   % (image, imgenvdir, native_sysroot,
                                      self.resultdir))
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct")))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_wic_image_type(self):
        """Test building wic images by bitbake"""
        config = 'IMAGE_FSTYPES += "wic"\nWKS_FILE = "wic-image-minimal"\n'\
                 'MACHINE_FEATURES_append = " efi"\n'
        self.append_config(config)
        self.assertEqual(0, bitbake('wic-image-minimal').status)
        self.remove_config(config)

        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'MACHINE'])
        deploy_dir = bb_vars['DEPLOY_DIR_IMAGE']
        machine = bb_vars['MACHINE']
        prefix = os.path.join(deploy_dir, 'wic-image-minimal-%s.' % machine)
        # check if we have result image and manifests symlinks
        # pointing to existing files
        for suffix in ('wic', 'manifest'):
            path = prefix + suffix
            self.assertTrue(os.path.islink(path))
            self.assertTrue(os.path.isfile(os.path.realpath(path)))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_qemu(self):
        """Test wic-image-minimal under qemu"""
        config = 'IMAGE_FSTYPES += "wic"\nWKS_FILE = "wic-image-minimal"\n'\
                 'MACHINE_FEATURES_append = " efi"\n'
        self.append_config(config)
        self.assertEqual(0, bitbake('wic-image-minimal').status)
        self.remove_config(config)

        with runqemu('wic-image-minimal', ssh=False) as qemu:
            cmd = "mount | grep '^/dev/' | cut -f1,3 -d ' ' | egrep -c -e '/dev/sda1 /boot' " \
                  "-e '/dev/root /|/dev/sda2 /' -e '/dev/sda3 /media' -e '/dev/sda4 /mnt'"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, '4')
            cmd = "grep UUID= /etc/fstab"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, 'UUID=2c71ef06-a81d-4735-9d3a-379b69c6bdba\t/media\text4\tdefaults\t0\t0')

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_qemu_efi(self):
        """Test core-image-minimal efi image under qemu"""
        config = 'IMAGE_FSTYPES = "wic"\nWKS_FILE = "mkefidisk.wks"\n'
        self.append_config(config)
        self.assertEqual(0, bitbake('core-image-minimal ovmf').status)
        self.remove_config(config)

        with runqemu('core-image-minimal', ssh=False,
                     runqemuparams='ovmf', image_fstype='wic') as qemu:
            cmd = "grep sda. /proc/partitions  |wc -l"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, '3')

    @staticmethod
    def _make_fixed_size_wks(size):
        """
        Create a wks of an image with a single partition. Size of the partition is set
        using --fixed-size flag. Returns a tuple: (path to wks file, wks image name)
        """
        with NamedTemporaryFile("w", suffix=".wks", delete=False) as tempf:
            wkspath = tempf.name
            tempf.write("part " \
                     "--source rootfs --ondisk hda --align 4 --fixed-size %d "
                     "--fstype=ext4\n" % size)

        return wkspath

    def _get_wic_partitions(self, wkspath, native_sysroot=None, ignore_status=False):
        p = runCmd("wic create %s -e core-image-minimal -o %s" % (wkspath, self.resultdir),
                   ignore_status=ignore_status)

        if p.status:
            return (p, None)

        wksname = os.path.splitext(os.path.basename(wkspath))[0]

        wicout = glob(self.resultdir + "%s-*direct" % wksname)

        if not wicout:
            return (p, None)

        wicimg = wicout[0]

        if not native_sysroot:
            native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "wic-tools")

        # verify partition size with wic
        res = runCmd("parted -m %s unit kib p 2>/dev/null" % wicimg,
                     native_sysroot=native_sysroot)

        # parse parted output which looks like this:
        # BYT;\n
        # /var/tmp/wic/build/tmpfwvjjkf_-201611101222-hda.direct:200MiB:file:512:512:msdos::;\n
        # 1:0.00MiB:200MiB:200MiB:ext4::;\n
        return (p, res.output.splitlines()[2:])

    def test_fixed_size(self):
        """
        Test creation of a simple image with partition size controlled through
        --fixed-size flag
        """
        wkspath = Wic2._make_fixed_size_wks(200)
        _, partlns = self._get_wic_partitions(wkspath)
        os.remove(wkspath)

        self.assertEqual(partlns, [
                        "1:4.00kiB:204804kiB:204800kiB:ext4::;",
                        ])

    def test_fixed_size_error(self):
        """
        Test creation of a simple image with partition size controlled through
        --fixed-size flag. The size of partition is intentionally set to 1MiB
        in order to trigger an error in wic.
        """
        wkspath = Wic2._make_fixed_size_wks(1)
        p, _ = self._get_wic_partitions(wkspath, ignore_status=True)
        os.remove(wkspath)

        self.assertNotEqual(p.status, 0, "wic exited successfully when an error was expected:\n%s" % p.output)

    def test_offset(self):
        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "wic-tools")

        with NamedTemporaryFile("w", suffix=".wks") as tempf:
            # Test that partitions are placed at the correct offsets, default KB
            tempf.write("bootloader --ptable gpt\n" \
                        "part /    --source rootfs --ondisk hda --offset 32     --fixed-size 100M --fstype=ext4\n" \
                        "part /bar                 --ondisk hda --offset 102432 --fixed-size 100M --fstype=ext4\n")
            tempf.flush()

            _, partlns = self._get_wic_partitions(tempf.name, native_sysroot)
            self.assertEqual(partlns, [
                "1:32.0kiB:102432kiB:102400kiB:ext4:primary:;",
                "2:102432kiB:204832kiB:102400kiB:ext4:primary:;",
                ])

        with NamedTemporaryFile("w", suffix=".wks") as tempf:
            # Test that partitions are placed at the correct offsets, same with explicit KB
            tempf.write("bootloader --ptable gpt\n" \
                        "part /    --source rootfs --ondisk hda --offset 32K     --fixed-size 100M --fstype=ext4\n" \
                        "part /bar                 --ondisk hda --offset 102432K --fixed-size 100M --fstype=ext4\n")
            tempf.flush()

            _, partlns = self._get_wic_partitions(tempf.name, native_sysroot)
            self.assertEqual(partlns, [
                "1:32.0kiB:102432kiB:102400kiB:ext4:primary:;",
                "2:102432kiB:204832kiB:102400kiB:ext4:primary:;",
                ])

        with NamedTemporaryFile("w", suffix=".wks") as tempf:
            # Test that partitions are placed at the correct offsets using MB
            tempf.write("bootloader --ptable gpt\n" \
                        "part /    --source rootfs --ondisk hda --offset 32K  --fixed-size 100M --fstype=ext4\n" \
                        "part /bar                 --ondisk hda --offset 101M --fixed-size 100M --fstype=ext4\n")
            tempf.flush()

            _, partlns = self._get_wic_partitions(tempf.name, native_sysroot)
            self.assertEqual(partlns, [
                "1:32.0kiB:102432kiB:102400kiB:ext4:primary:;",
                "2:103424kiB:205824kiB:102400kiB:ext4:primary:;",
                ])

        with NamedTemporaryFile("w", suffix=".wks") as tempf:
            # Test that partitions can be placed on a 512 byte sector boundary
            tempf.write("bootloader --ptable gpt\n" \
                        "part /    --source rootfs --ondisk hda --offset 65s --fixed-size 99M --fstype=ext4\n" \
                        "part /bar                 --ondisk hda --offset 102432 --fixed-size 100M --fstype=ext4\n")
            tempf.flush()

            _, partlns = self._get_wic_partitions(tempf.name, native_sysroot)
            self.assertEqual(partlns, [
                "1:32.5kiB:101408kiB:101376kiB:ext4:primary:;",
                "2:102432kiB:204832kiB:102400kiB:ext4:primary:;",
                ])

        with NamedTemporaryFile("w", suffix=".wks") as tempf:
            # Test that a partition can be placed immediately after a MSDOS partition table
            tempf.write("bootloader --ptable msdos\n" \
                        "part /    --source rootfs --ondisk hda --offset 1s --fixed-size 100M --fstype=ext4\n")
            tempf.flush()

            _, partlns = self._get_wic_partitions(tempf.name, native_sysroot)
            self.assertEqual(partlns, [
                "1:0.50kiB:102400kiB:102400kiB:ext4::;",
                ])

        with NamedTemporaryFile("w", suffix=".wks") as tempf:
            # Test that image creation fails if the partitions would overlap
            tempf.write("bootloader --ptable gpt\n" \
                        "part /    --source rootfs --ondisk hda --offset 32     --fixed-size 100M --fstype=ext4\n" \
                        "part /bar                 --ondisk hda --offset 102431 --fixed-size 100M --fstype=ext4\n")
            tempf.flush()

            p, _ = self._get_wic_partitions(tempf.name, ignore_status=True)
            self.assertNotEqual(p.status, 0, "wic exited successfully when an error was expected:\n%s" % p.output)

        with NamedTemporaryFile("w", suffix=".wks") as tempf:
            # Test that partitions are not allowed to overlap with the booloader
            tempf.write("bootloader --ptable gpt\n" \
                        "part /    --source rootfs --ondisk hda --offset 8 --fixed-size 100M --fstype=ext4\n")
            tempf.flush()

            p, _ = self._get_wic_partitions(tempf.name, ignore_status=True)
            self.assertNotEqual(p.status, 0, "wic exited successfully when an error was expected:\n%s" % p.output)

    def test_extra_space(self):
        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "wic-tools")

        with NamedTemporaryFile("w", suffix=".wks") as tempf:
            tempf.write("bootloader --ptable gpt\n" \
                        "part /     --source rootfs --ondisk hda --extra-space 200M --fstype=ext4\n")
            tempf.flush()

            _, partlns = self._get_wic_partitions(tempf.name, native_sysroot)
            self.assertEqual(len(partlns), 1)
            size = partlns[0].split(':')[3]
            self.assertRegex(size, r'^[0-9]+kiB$')
            size = int(size[:-3])
            self.assertGreaterEqual(size, 204800)

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_rawcopy_plugin_qemu(self):
        """Test rawcopy plugin in qemu"""
        # build ext4 and wic images
        for fstype in ("ext4", "wic"):
            config = 'IMAGE_FSTYPES = "%s"\nWKS_FILE = "test_rawcopy_plugin.wks.in"\n' % fstype
            self.append_config(config)
            self.assertEqual(0, bitbake('core-image-minimal').status)
            self.remove_config(config)

        with runqemu('core-image-minimal', ssh=False, image_fstype='wic') as qemu:
            cmd = "grep sda. /proc/partitions  |wc -l"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, '2')

    def test_rawcopy_plugin(self):
        """Test rawcopy plugin"""
        img = 'core-image-minimal'
        machine = get_bb_var('MACHINE', img)
        with NamedTemporaryFile("w", suffix=".wks") as wks:
            wks.writelines(['part /boot --active --source bootimg-pcbios\n',
                            'part / --source rawcopy --sourceparams="file=%s-%s.ext4" --use-uuid\n'\
                             % (img, machine),
                            'bootloader --timeout=0 --append="console=ttyS0,115200n8"\n'])
            wks.flush()
            cmd = "wic create %s -e %s -o %s" % (wks.name, img, self.resultdir)
            runCmd(cmd)
            wksname = os.path.splitext(os.path.basename(wks.name))[0]
            out = glob(self.resultdir + "%s-*direct" % wksname)
            self.assertEqual(1, len(out))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_biosplusefi_plugin_qemu(self):
        """Test biosplusefi plugin in qemu"""
        config = 'IMAGE_FSTYPES = "wic"\nWKS_FILE = "test_biosplusefi_plugin.wks"\nMACHINE_FEATURES_append = " efi"\n'
        self.append_config(config)
        self.assertEqual(0, bitbake('core-image-minimal').status)
        self.remove_config(config)

        with runqemu('core-image-minimal', ssh=False, image_fstype='wic') as qemu:
            # Check that we have ONLY two /dev/sda* partitions (/boot and /)
            cmd = "grep sda. /proc/partitions | wc -l"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, '2')
            # Check that /dev/sda1 is /boot and that either /dev/root OR /dev/sda2 is /
            cmd = "mount | grep '^/dev/' | cut -f1,3 -d ' ' | egrep -c -e '/dev/sda1 /boot' -e '/dev/root /|/dev/sda2 /'"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, '2')
            # Check that /boot has EFI bootx64.efi (required for EFI)
            cmd = "ls /boot/EFI/BOOT/bootx64.efi | wc -l"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, '1')
            # Check that "BOOTABLE" flag is set on boot partition (required for PC-Bios)
            # Trailing "cat" seems to be required; otherwise run_serial() sends back echo of the input command
            cmd = "fdisk -l /dev/sda | grep /dev/sda1 | awk {print'$2'} | cat"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, '*')

    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_biosplusefi_plugin(self):
        """Test biosplusefi plugin"""
        # Wic generation below may fail depending on the order of the unittests
        # This is because bootimg-pcbios (that bootimg-biosplusefi uses) generate its MBR inside STAGING_DATADIR directory
        #    which may or may not exists depending on what was built already
        # If an image hasn't been built yet, directory ${STAGING_DATADIR}/syslinux won't exists and _get_bootimg_dir()
        #   will raise with "Couldn't find correct bootimg_dir"
        # The easiest way to work-around this issue is to make sure we already built an image here, hence the bitbake call
        config = 'IMAGE_FSTYPES = "wic"\nWKS_FILE = "test_biosplusefi_plugin.wks"\nMACHINE_FEATURES_append = " efi"\n'
        self.append_config(config)
        self.assertEqual(0, bitbake('core-image-minimal').status)
        self.remove_config(config)

        img = 'core-image-minimal'
        with NamedTemporaryFile("w", suffix=".wks") as wks:
            wks.writelines(['part /boot --active --source bootimg-biosplusefi --sourceparams="loader=grub-efi"\n',
                            'part / --source rootfs --fstype=ext4 --align 1024 --use-uuid\n'\
                            'bootloader --timeout=0 --append="console=ttyS0,115200n8"\n'])
            wks.flush()
            cmd = "wic create %s -e %s -o %s" % (wks.name, img, self.resultdir)
            runCmd(cmd)
            wksname = os.path.splitext(os.path.basename(wks.name))[0]
            out = glob(self.resultdir + "%s-*.direct" % wksname)
            self.assertEqual(1, len(out))

    def test_fs_types(self):
        """Test filesystem types for empty and not empty partitions"""
        img = 'core-image-minimal'
        with NamedTemporaryFile("w", suffix=".wks") as wks:
            wks.writelines(['part ext2   --fstype ext2     --source rootfs\n',
                            'part btrfs  --fstype btrfs    --source rootfs --size 40M\n',
                            'part squash --fstype squashfs --source rootfs\n',
                            'part swap   --fstype swap --size 1M\n',
                            'part emptyvfat   --fstype vfat   --size 1M\n',
                            'part emptymsdos  --fstype msdos  --size 1M\n',
                            'part emptyext2   --fstype ext2   --size 1M\n',
                            'part emptybtrfs  --fstype btrfs  --size 150M\n'])
            wks.flush()
            cmd = "wic create %s -e %s -o %s" % (wks.name, img, self.resultdir)
            runCmd(cmd)
            wksname = os.path.splitext(os.path.basename(wks.name))[0]
            out = glob(self.resultdir + "%s-*direct" % wksname)
            self.assertEqual(1, len(out))

    def test_kickstart_parser(self):
        """Test wks parser options"""
        with NamedTemporaryFile("w", suffix=".wks") as wks:
            wks.writelines(['part / --fstype ext3 --source rootfs --system-id 0xFF '\
                            '--overhead-factor 1.2 --size 100k\n'])
            wks.flush()
            cmd = "wic create %s -e core-image-minimal -o %s" % (wks.name, self.resultdir)
            runCmd(cmd)
            wksname = os.path.splitext(os.path.basename(wks.name))[0]
            out = glob(self.resultdir + "%s-*direct" % wksname)
            self.assertEqual(1, len(out))

    def test_image_bootpart_globbed(self):
        """Test globbed sources with image-bootpart plugin"""
        img = "core-image-minimal"
        cmd = "wic create sdimage-bootpart -e %s -o %s" % (img, self.resultdir)
        config = 'IMAGE_BOOT_FILES = "%s*"' % get_bb_var('KERNEL_IMAGETYPE', img)
        self.append_config(config)
        runCmd(cmd)
        self.remove_config(config)
        self.assertEqual(1, len(glob(self.resultdir + "sdimage-bootpart-*direct")))

    def test_sparse_copy(self):
        """Test sparse_copy with FIEMAP and SEEK_HOLE filemap APIs"""
        libpath = os.path.join(get_bb_var('COREBASE'), 'scripts', 'lib', 'wic')
        sys.path.insert(0, libpath)
        from  filemap import FilemapFiemap, FilemapSeek, sparse_copy, ErrorNotSupp
        with NamedTemporaryFile("w", suffix=".wic-sparse") as sparse:
            src_name = sparse.name
            src_size = 1024 * 10
            sparse.truncate(src_size)
            # write one byte to the file
            with open(src_name, 'r+b') as sfile:
                sfile.seek(1024 * 4)
                sfile.write(b'\x00')
            dest = sparse.name + '.out'
            # copy src file to dest using different filemap APIs
            for api in (FilemapFiemap, FilemapSeek, None):
                if os.path.exists(dest):
                    os.unlink(dest)
                try:
                    sparse_copy(sparse.name, dest, api=api)
                except ErrorNotSupp:
                    continue # skip unsupported API
                dest_stat = os.stat(dest)
                self.assertEqual(dest_stat.st_size, src_size)
                # 8 blocks is 4K (physical sector size)
                self.assertEqual(dest_stat.st_blocks, 8)
            os.unlink(dest)

    def test_wic_ls(self):
        """Test listing image content using 'wic ls'"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir)
        images = glob(self.resultdir + "wictestdisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list partitions
        result = runCmd("wic ls %s -n %s" % (images[0], sysroot))
        self.assertEqual(3, len(result.output.split('\n')))

        # list directory content of the first partition
        result = runCmd("wic ls %s:1/ -n %s" % (images[0], sysroot))
        self.assertEqual(6, len(result.output.split('\n')))

    def test_wic_cp(self):
        """Test copy files and directories to the the wic image."""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir)
        images = glob(self.resultdir + "wictestdisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the first partition
        result = runCmd("wic ls %s:1/ -n %s" % (images[0], sysroot))
        self.assertEqual(6, len(result.output.split('\n')))

        with NamedTemporaryFile("w", suffix=".wic-cp") as testfile:
            testfile.write("test")

            # copy file to the partition
            runCmd("wic cp %s %s:1/ -n %s" % (testfile.name, images[0], sysroot))

            # check if file is there
            result = runCmd("wic ls %s:1/ -n %s" % (images[0], sysroot))
            self.assertEqual(7, len(result.output.split('\n')))
            self.assertTrue(os.path.basename(testfile.name) in result.output)

            # prepare directory
            testdir = os.path.join(self.resultdir, 'wic-test-cp-dir')
            testsubdir = os.path.join(testdir, 'subdir')
            os.makedirs(os.path.join(testsubdir))
            copy(testfile.name, testdir)

            # copy directory to the partition
            runCmd("wic cp %s %s:1/ -n %s" % (testdir, images[0], sysroot))

            # check if directory is there
            result = runCmd("wic ls %s:1/ -n %s" % (images[0], sysroot))
            self.assertEqual(8, len(result.output.split('\n')))
            self.assertTrue(os.path.basename(testdir) in result.output)

            # copy the file from the partition and check if it success
            dest = '%s-cp' % testfile.name
            runCmd("wic cp %s:1/%s %s -n %s" % (images[0],
                    os.path.basename(testfile.name), dest, sysroot))
            self.assertTrue(os.path.exists(dest))


    def test_wic_rm(self):
        """Test removing files and directories from the the wic image."""
        runCmd("wic create mkefidisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir)
        images = glob(self.resultdir + "mkefidisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the first partition
        result = runCmd("wic ls %s:1 -n %s" % (images[0], sysroot))
        self.assertIn('\nBZIMAGE        ', result.output)
        self.assertIn('\nEFI          <DIR>     ', result.output)

        # remove file
        runCmd("wic rm %s:1/bzimage -n %s" % (images[0], sysroot))

        # remove directory
        runCmd("wic rm %s:1/efi -n %s" % (images[0], sysroot))

        # check if they're removed
        result = runCmd("wic ls %s:1 -n %s" % (images[0], sysroot))
        self.assertNotIn('\nBZIMAGE        ', result.output)
        self.assertNotIn('\nEFI          <DIR>     ', result.output)

    def test_mkfs_extraopts(self):
        """Test wks option --mkfs-extraopts for empty and not empty partitions"""
        img = 'core-image-minimal'
        with NamedTemporaryFile("w", suffix=".wks") as wks:
            wks.writelines(
                ['part ext2   --fstype ext2     --source rootfs --mkfs-extraopts "-D -F -i 8192"\n',
                 "part btrfs  --fstype btrfs    --source rootfs --size 40M --mkfs-extraopts='--quiet'\n",
                 'part squash --fstype squashfs --source rootfs --mkfs-extraopts "-no-sparse -b 4096"\n',
                 'part emptyvfat   --fstype vfat   --size 1M --mkfs-extraopts "-S 1024 -s 64"\n',
                 'part emptymsdos  --fstype msdos  --size 1M --mkfs-extraopts "-S 1024 -s 64"\n',
                 'part emptyext2   --fstype ext2   --size 1M --mkfs-extraopts "-D -F -i 8192"\n',
                 'part emptybtrfs  --fstype btrfs  --size 100M --mkfs-extraopts "--mixed -K"\n'])
            wks.flush()
            cmd = "wic create %s -e %s -o %s" % (wks.name, img, self.resultdir)
            runCmd(cmd)
            wksname = os.path.splitext(os.path.basename(wks.name))[0]
            out = glob(self.resultdir + "%s-*direct" % wksname)
            self.assertEqual(1, len(out))

    def test_expand_mbr_image(self):
        """Test wic write --expand command for mbr image"""
        # build an image
        config = 'IMAGE_FSTYPES = "wic"\nWKS_FILE = "directdisk.wks"\n'
        self.append_config(config)
        self.assertEqual(0, bitbake('core-image-minimal').status)

        # get path to the image
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'MACHINE'])
        deploy_dir = bb_vars['DEPLOY_DIR_IMAGE']
        machine = bb_vars['MACHINE']
        image_path = os.path.join(deploy_dir, 'core-image-minimal-%s.wic' % machine)

        self.remove_config(config)

        try:
            # expand image to 1G
            new_image_path = None
            with NamedTemporaryFile(mode='wb', suffix='.wic.exp',
                                    dir=deploy_dir, delete=False) as sparse:
                sparse.truncate(1024 ** 3)
                new_image_path = sparse.name

            sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')
            cmd = "wic write -n %s --expand 1:0 %s %s" % (sysroot, image_path, new_image_path)
            runCmd(cmd)

            # check if partitions are expanded
            orig = runCmd("wic ls %s -n %s" % (image_path, sysroot))
            exp = runCmd("wic ls %s -n %s" % (new_image_path, sysroot))
            orig_sizes = [int(line.split()[3]) for line in orig.output.split('\n')[1:]]
            exp_sizes = [int(line.split()[3]) for line in exp.output.split('\n')[1:]]
            self.assertEqual(orig_sizes[0], exp_sizes[0]) # first partition is not resized
            self.assertTrue(orig_sizes[1] < exp_sizes[1])

            # Check if all free space is partitioned
            result = runCmd("%s/usr/sbin/sfdisk -F %s" % (sysroot, new_image_path))
            self.assertTrue("0 B, 0 bytes, 0 sectors" in result.output)

            os.rename(image_path, image_path + '.bak')
            os.rename(new_image_path, image_path)

            # Check if it boots in qemu
            with runqemu('core-image-minimal', ssh=False) as qemu:
                cmd = "ls /etc/"
                status, output = qemu.run_serial('true')
                self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
        finally:
            if os.path.exists(new_image_path):
                os.unlink(new_image_path)
            if os.path.exists(image_path + '.bak'):
                os.rename(image_path + '.bak', image_path)

    def test_wic_ls_ext(self):
        """Test listing content of the ext partition using 'wic ls'"""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir)
        images = glob(self.resultdir + "wictestdisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the second ext4 partition
        result = runCmd("wic ls %s:2/ -n %s" % (images[0], sysroot))
        self.assertTrue(set(['bin', 'home', 'proc', 'usr', 'var', 'dev', 'lib', 'sbin']).issubset(
                            set(line.split()[-1] for line in result.output.split('\n') if line)))

    def test_wic_cp_ext(self):
        """Test copy files and directories to the ext partition."""
        runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir)
        images = glob(self.resultdir + "wictestdisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the ext4 partition
        result = runCmd("wic ls %s:2/ -n %s" % (images[0], sysroot))
        dirs = set(line.split()[-1] for line in result.output.split('\n') if line)
        self.assertTrue(set(['bin', 'home', 'proc', 'usr', 'var', 'dev', 'lib', 'sbin']).issubset(dirs))

        with NamedTemporaryFile("w", suffix=".wic-cp") as testfile:
            testfile.write("test")

            # copy file to the partition
            runCmd("wic cp %s %s:2/ -n %s" % (testfile.name, images[0], sysroot))

            # check if file is there
            result = runCmd("wic ls %s:2/ -n %s" % (images[0], sysroot))
            newdirs = set(line.split()[-1] for line in result.output.split('\n') if line)
            self.assertEqual(newdirs.difference(dirs), set([os.path.basename(testfile.name)]))

            # check if the file to copy is in the partition
            result = runCmd("wic ls %s:2/etc/ -n %s" % (images[0], sysroot))
            self.assertTrue('fstab' in [line.split()[-1] for line in result.output.split('\n') if line])

            # copy file from the partition, replace the temporary file content with it and
            # check for the file size to validate the copy
            runCmd("wic cp %s:2/etc/fstab %s -n %s" % (images[0], testfile.name, sysroot))
            self.assertTrue(os.stat(testfile.name).st_size > 0)


    def test_wic_rm_ext(self):
        """Test removing files from the ext partition."""
        runCmd("wic create mkefidisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir)
        images = glob(self.resultdir + "mkefidisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the /etc directory on ext4 partition
        result = runCmd("wic ls %s:2/etc/ -n %s" % (images[0], sysroot))
        self.assertTrue('fstab' in [line.split()[-1] for line in result.output.split('\n') if line])

        # remove file
        runCmd("wic rm %s:2/etc/fstab -n %s" % (images[0], sysroot))

        # check if it's removed
        result = runCmd("wic ls %s:2/etc/ -n %s" % (images[0], sysroot))
        self.assertTrue('fstab' not in [line.split()[-1] for line in result.output.split('\n') if line])

        # remove non-empty directory
        runCmd("wic rm -r %s:2/etc/ -n %s" % (images[0], sysroot))

        # check if it's removed
        result = runCmd("wic ls %s:2/ -n %s" % (images[0], sysroot))
        self.assertTrue('etc' not in [line.split()[-1] for line in result.output.split('\n') if line])
