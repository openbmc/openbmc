#!/usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2015, Intel Corporation.
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
from oeqa.core.decorator.oeid import OETestID


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


class Wic(OESelftestTestCase):
    """Wic test class."""

    image_is_ready = False
    native_sysroot = None
    wicenv_cache = {}

    def setUpLocal(self):
        """This code is executed before each test method."""
        self.resultdir = self.builddir + "/wic-tmp/"
        super(Wic, self).setUpLocal()
        if not self.native_sysroot:
            Wic.native_sysroot = get_bb_var('STAGING_DIR_NATIVE', 'wic-tools')

        # Do this here instead of in setUpClass as the base setUp does some
        # clean up which can result in the native tools built earlier in
        # setUpClass being unavailable.
        if not Wic.image_is_ready:
            if get_bb_var('USE_NLS') == 'yes':
                bitbake('wic-tools')
            else:
                self.skipTest('wic-tools cannot be built due its (intltool|gettext)-native dependency and NLS disable')

            bitbake('core-image-minimal')
            Wic.image_is_ready = True

        rmtree(self.resultdir, ignore_errors=True)

    def tearDownLocal(self):
        """Remove resultdir as it may contain images."""
        rmtree(self.resultdir, ignore_errors=True)
        super(Wic, self).tearDownLocal()

    @OETestID(1552)
    def test_version(self):
        """Test wic --version"""
        self.assertEqual(0, runCmd('wic --version').status)

    @OETestID(1208)
    def test_help(self):
        """Test wic --help and wic -h"""
        self.assertEqual(0, runCmd('wic --help').status)
        self.assertEqual(0, runCmd('wic -h').status)

    @OETestID(1209)
    def test_createhelp(self):
        """Test wic create --help"""
        self.assertEqual(0, runCmd('wic create --help').status)

    @OETestID(1210)
    def test_listhelp(self):
        """Test wic list --help"""
        self.assertEqual(0, runCmd('wic list --help').status)

    @OETestID(1553)
    def test_help_create(self):
        """Test wic help create"""
        self.assertEqual(0, runCmd('wic help create').status)

    @OETestID(1554)
    def test_help_list(self):
        """Test wic help list"""
        self.assertEqual(0, runCmd('wic help list').status)

    @OETestID(1215)
    def test_help_overview(self):
        """Test wic help overview"""
        self.assertEqual(0, runCmd('wic help overview').status)

    @OETestID(1216)
    def test_help_plugins(self):
        """Test wic help plugins"""
        self.assertEqual(0, runCmd('wic help plugins').status)

    @OETestID(1217)
    def test_help_kickstart(self):
        """Test wic help kickstart"""
        self.assertEqual(0, runCmd('wic help kickstart').status)

    @OETestID(1555)
    def test_list_images(self):
        """Test wic list images"""
        self.assertEqual(0, runCmd('wic list images').status)

    @OETestID(1556)
    def test_list_source_plugins(self):
        """Test wic list source-plugins"""
        self.assertEqual(0, runCmd('wic list source-plugins').status)

    @OETestID(1557)
    def test_listed_images_help(self):
        """Test wic listed images help"""
        output = runCmd('wic list images').output
        imagelist = [line.split()[0] for line in output.splitlines()]
        for image in imagelist:
            self.assertEqual(0, runCmd('wic list %s help' % image).status)

    @OETestID(1213)
    def test_unsupported_subcommand(self):
        """Test unsupported subcommand"""
        self.assertNotEqual(0, runCmd('wic unsupported', ignore_status=True).status)

    @OETestID(1214)
    def test_no_command(self):
        """Test wic without command"""
        self.assertEqual(1, runCmd('wic', ignore_status=True).status)

    @OETestID(1211)
    def test_build_image_name(self):
        """Test wic create wictestdisk --image-name=core-image-minimal"""
        cmd = "wic create wictestdisk --image-name=core-image-minimal -o %s" % self.resultdir
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @OETestID(1157)
    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_gpt_image(self):
        """Test creation of core-image-minimal with gpt table and UUID boot"""
        cmd = "wic create directdisk-gpt --image-name core-image-minimal -o %s" % self.resultdir
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-*.direct")))

    @OETestID(1346)
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
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob(self.resultdir + "HYBRID_ISO_IMG-*.direct")))
        self.assertEqual(1, len(glob(self.resultdir + "HYBRID_ISO_IMG-*.iso")))

    @OETestID(1348)
    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_qemux86_directdisk(self):
        """Test creation of qemux-86-directdisk image"""
        cmd = "wic create qemux86-directdisk -e core-image-minimal -o %s" % self.resultdir
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob(self.resultdir + "qemux86-directdisk-*direct")))

    @OETestID(1350)
    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_mkefidisk(self):
        """Test creation of mkefidisk image"""
        cmd = "wic create mkefidisk -e core-image-minimal -o %s" % self.resultdir
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob(self.resultdir + "mkefidisk-*direct")))

    @OETestID(1385)
    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_bootloader_config(self):
        """Test creation of directdisk-bootloader-config image"""
        config = 'DEPENDS_pn-core-image-minimal += "syslinux"\n'
        self.append_config(config)
        bitbake('core-image-minimal')
        self.remove_config(config)
        cmd = "wic create directdisk-bootloader-config -e core-image-minimal -o %s" % self.resultdir
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-bootloader-config-*direct")))

    @OETestID(1560)
    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_systemd_bootdisk(self):
        """Test creation of systemd-bootdisk image"""
        config = 'MACHINE_FEATURES_append = " efi"\n'
        self.append_config(config)
        bitbake('core-image-minimal')
        self.remove_config(config)
        cmd = "wic create systemd-bootdisk -e core-image-minimal -o %s" % self.resultdir
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob(self.resultdir + "systemd-bootdisk-*direct")))

    @OETestID(1561)
    def test_sdimage_bootpart(self):
        """Test creation of sdimage-bootpart image"""
        cmd = "wic create sdimage-bootpart -e core-image-minimal -o %s" % self.resultdir
        kimgtype = get_bb_var('KERNEL_IMAGETYPE', 'core-image-minimal')
        self.write_config('IMAGE_BOOT_FILES = "%s"\n' % kimgtype)
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob(self.resultdir + "sdimage-bootpart-*direct")))

    @OETestID(1562)
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
        self.assertEqual(0, runCmd(cmd).status)
        self.assertEqual(1, len(glob("directdisk-*.direct")))

    @OETestID(1212)
    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_build_artifacts(self):
        """Test wic create directdisk providing all artifacts."""
        bb_vars = get_bb_vars(['STAGING_DATADIR', 'RECIPE_SYSROOT_NATIVE'],
                              'wic-tools')
        bb_vars.update(get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_ROOTFS'],
                                   'core-image-minimal'))
        bbvars = {key.lower(): value for key, value in bb_vars.items()}
        bbvars['resultdir'] = self.resultdir
        status = runCmd("wic create directdisk "
                        "-b %(staging_datadir)s "
                        "-k %(deploy_dir_image)s "
                        "-n %(recipe_sysroot_native)s "
                        "-r %(image_rootfs)s "
                        "-o %(resultdir)s" % bbvars).status
        self.assertEqual(0, status)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-*.direct")))

    @OETestID(1264)
    def test_compress_gzip(self):
        """Test compressing an image with gzip"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name core-image-minimal "
                                   "-c gzip -o %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct.gz")))

    @OETestID(1265)
    def test_compress_bzip2(self):
        """Test compressing an image with bzip2"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-c bzip2 -o %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct.bz2")))

    @OETestID(1266)
    def test_compress_xz(self):
        """Test compressing an image with xz"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "--compress-with=xz -o %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct.xz")))

    @OETestID(1267)
    def test_wrong_compressor(self):
        """Test how wic breaks if wrong compressor is provided"""
        self.assertEqual(2, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-c wrong -o %s" % self.resultdir,
                                   ignore_status=True).status)

    @OETestID(1558)
    def test_debug_short(self):
        """Test -D option"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @OETestID(1658)
    def test_debug_long(self):
        """Test --debug option"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "--debug -o %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @OETestID(1563)
    def test_skip_build_check_short(self):
        """Test -s option"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-s -o %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @OETestID(1671)
    def test_skip_build_check_long(self):
        """Test --skip-build-check option"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "--skip-build-check "
                                   "--outdir %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @OETestID(1564)
    def test_build_rootfs_short(self):
        """Test -f option"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-f -o %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @OETestID(1656)
    def test_build_rootfs_long(self):
        """Test --build-rootfs option"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "--build-rootfs "
                                   "--outdir %s" % self.resultdir).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*.direct")))

    @OETestID(1268)
    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_rootfs_indirect_recipes(self):
        """Test usage of rootfs plugin with rootfs recipes"""
        status = runCmd("wic create directdisk-multi-rootfs "
                        "--image-name=core-image-minimal "
                        "--rootfs rootfs1=core-image-minimal "
                        "--rootfs rootfs2=core-image-minimal "
                        "--outdir %s" % self.resultdir).status
        self.assertEqual(0, status)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-multi-rootfs*.direct")))

    @OETestID(1269)
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
        status = runCmd("wic create %(wks)s "
                        "--bootimg-dir=%(staging_datadir)s "
                        "--kernel-dir=%(deploy_dir_image)s "
                        "--native-sysroot=%(recipe_sysroot_native)s "
                        "--rootfs-dir rootfs1=%(image_rootfs)s "
                        "--rootfs-dir rootfs2=%(image_rootfs)s "
                        "--outdir %(resultdir)s" % bbvars).status
        self.assertEqual(0, status)
        self.assertEqual(1, len(glob(self.resultdir + "%(wks)s-*.direct" % bbvars)))

    @OETestID(1661)
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
            self.assertEqual(0, runCmd("wic create %s -e core-image-minimal -o %s" \
                                       % (wks_file, self.resultdir)).status)

            os.remove(wks_file)
            wicout = glob(self.resultdir + "%s-*direct" % 'temp')
            self.assertEqual(1, len(wicout))

            wicimg = wicout[0]

            # verify partition size with wic
            res = runCmd("parted -m %s unit b p 2>/dev/null" % wicimg)
            self.assertEqual(0, res.status)

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
                self.assertEqual(0, runCmd("dd if=%s of=%s skip=%d count=%d" %
                                           (wicimg, part_file, start, length)).status)

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

            # Test partition 1, should contain the normal root directories, except
            # /usr.
            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % \
                             os.path.join(self.resultdir, "selftest_img.part1"))
            self.assertEqual(0, res.status)
            files = extract_files(res.output)
            self.assertIn("etc", files)
            self.assertNotIn("usr", files)

            # Partition 2, should contain common directories for /usr, not root
            # directories.
            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % \
                             os.path.join(self.resultdir, "selftest_img.part2"))
            self.assertEqual(0, res.status)
            files = extract_files(res.output)
            self.assertNotIn("etc", files)
            self.assertNotIn("usr", files)
            self.assertIn("share", files)

            # Partition 3, should contain the same as partition 2, including the bin
            # directory, but not the files inside it.
            res = runCmd("debugfs -R 'ls -p' %s 2>/dev/null" % \
                             os.path.join(self.resultdir, "selftest_img.part3"))
            self.assertEqual(0, res.status)
            files = extract_files(res.output)
            self.assertNotIn("etc", files)
            self.assertNotIn("usr", files)
            self.assertIn("share", files)
            self.assertIn("bin", files)
            res = runCmd("debugfs -R 'ls -p bin' %s 2>/dev/null" % \
                             os.path.join(self.resultdir, "selftest_img.part3"))
            self.assertEqual(0, res.status)
            files = extract_files(res.output)
            self.assertIn(".", files)
            self.assertIn("..", files)
            self.assertEqual(2, len(files))

            for part in [1, 2, 3]:
                part_file = os.path.join(self.resultdir, "selftest_img.part%d" % part)
                os.remove(part_file)

        finally:
            os.environ['PATH'] = oldpath

    @OETestID(1662)
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

    @OETestID(1496)
    def test_bmap_short(self):
        """Test generation of .bmap file -m option"""
        cmd = "wic create wictestdisk -e core-image-minimal -m -o %s" % self.resultdir
        status = runCmd(cmd).status
        self.assertEqual(0, status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct")))
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct.bmap")))

    @OETestID(1655)
    def test_bmap_long(self):
        """Test generation of .bmap file --bmap option"""
        cmd = "wic create wictestdisk -e core-image-minimal --bmap -o %s" % self.resultdir
        status = runCmd(cmd).status
        self.assertEqual(0, status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct")))
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct.bmap")))

    def _get_image_env_path(self, image):
        """Generate and obtain the path to <image>.env"""
        if image not in self.wicenv_cache:
            self.assertEqual(0, bitbake('%s -c do_rootfs_wicenv' % image).status)
            bb_vars = get_bb_vars(['STAGING_DIR', 'MACHINE'], image)
            stdir = bb_vars['STAGING_DIR']
            machine = bb_vars['MACHINE']
            self.wicenv_cache[image] = os.path.join(stdir, machine, 'imgdata')
        return self.wicenv_cache[image]

    @OETestID(1347)
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
                                      'INITRD', 'INITRD_LIVE', 'ISODIR'))
        with open(path) as envfile:
            content = dict(line.split("=", 1) for line in envfile)
            # test if variables used by wic present in the .env file
            for var in wicvars:
                self.assertTrue(var in content, "%s is not in .env file" % var)
                self.assertTrue(content[var])

    @OETestID(1559)
    def test_image_vars_dir_short(self):
        """Test image vars directory selection -v option"""
        image = 'core-image-minimal'
        imgenvdir = self._get_image_env_path(image)
        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "wic-tools")

        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=%s -v %s -n %s -o %s"
                                   % (image, imgenvdir, native_sysroot,
                                      self.resultdir)).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct")))

    @OETestID(1665)
    def test_image_vars_dir_long(self):
        """Test image vars directory selection --vars option"""
        image = 'core-image-minimal'
        imgenvdir = self._get_image_env_path(image)
        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "wic-tools")

        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=%s "
                                   "--vars %s "
                                   "--native-sysroot %s "
                                   "--outdir %s"
                                   % (image, imgenvdir, native_sysroot,
                                      self.resultdir)).status)
        self.assertEqual(1, len(glob(self.resultdir + "wictestdisk-*direct")))

    @OETestID(1351)
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

    @OETestID(1424)
    @only_for_arch(['i586', 'i686', 'x86_64'])
    def test_qemu(self):
        """Test wic-image-minimal under qemu"""
        config = 'IMAGE_FSTYPES += "wic"\nWKS_FILE = "wic-image-minimal"\n'\
                 'MACHINE_FEATURES_append = " efi"\n'
        self.append_config(config)
        self.assertEqual(0, bitbake('wic-image-minimal').status)
        self.remove_config(config)

        with runqemu('wic-image-minimal', ssh=False) as qemu:
            cmd = "mount |grep '^/dev/' | cut -f1,3 -d ' ' | sort"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(output, '/dev/root /\r\n/dev/sda1 /boot\r\n/dev/sda3 /media\r\n/dev/sda4 /mnt')
            cmd = "grep UUID= /etc/fstab"
            status, output = qemu.run_serial(cmd)
            self.assertEqual(1, status, 'Failed to run command "%s": %s' % (cmd, output))
            self.assertEqual(output, 'UUID=2c71ef06-a81d-4735-9d3a-379b69c6bdba\t/media\text4\tdefaults\t0\t0')

    @only_for_arch(['i586', 'i686', 'x86_64'])
    @OETestID(1852)
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
        wksname = os.path.splitext(os.path.basename(wkspath))[0]

        return wkspath, wksname

    @OETestID(1847)
    def test_fixed_size(self):
        """
        Test creation of a simple image with partition size controlled through
        --fixed-size flag
        """
        wkspath, wksname = Wic._make_fixed_size_wks(200)

        self.assertEqual(0, runCmd("wic create %s -e core-image-minimal -o %s" \
                                   % (wkspath, self.resultdir)).status)
        os.remove(wkspath)
        wicout = glob(self.resultdir + "%s-*direct" % wksname)
        self.assertEqual(1, len(wicout))

        wicimg = wicout[0]

        # verify partition size with wic
        res = runCmd("parted -m %s unit mib p 2>/dev/null" % wicimg,
                     native_sysroot=self.native_sysroot)

        # parse parted output which looks like this:
        # BYT;\n
        # /var/tmp/wic/build/tmpfwvjjkf_-201611101222-hda.direct:200MiB:file:512:512:msdos::;\n
        # 1:0.00MiB:200MiB:200MiB:ext4::;\n
        partlns = res.output.splitlines()[2:]

        self.assertEqual(1, len(partlns))
        self.assertEqual("1:0.00MiB:200MiB:200MiB:ext4::;", partlns[0])

    @OETestID(1848)
    def test_fixed_size_error(self):
        """
        Test creation of a simple image with partition size controlled through
        --fixed-size flag. The size of partition is intentionally set to 1MiB
        in order to trigger an error in wic.
        """
        wkspath, wksname = Wic._make_fixed_size_wks(1)

        self.assertEqual(1, runCmd("wic create %s -e core-image-minimal -o %s" \
                                   % (wkspath, self.resultdir), ignore_status=True).status)
        os.remove(wkspath)
        wicout = glob(self.resultdir + "%s-*direct" % wksname)
        self.assertEqual(0, len(wicout))

    @only_for_arch(['i586', 'i686', 'x86_64'])
    @OETestID(1854)
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

    @OETestID(1853)
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
            self.assertEqual(0, runCmd(cmd).status)
            wksname = os.path.splitext(os.path.basename(wks.name))[0]
            out = glob(self.resultdir + "%s-*direct" % wksname)
            self.assertEqual(1, len(out))

    @OETestID(1849)
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
                            'part emptybtrfs  --fstype btrfs  --size 100M\n'])
            wks.flush()
            cmd = "wic create %s -e %s -o %s" % (wks.name, img, self.resultdir)
            self.assertEqual(0, runCmd(cmd).status)
            wksname = os.path.splitext(os.path.basename(wks.name))[0]
            out = glob(self.resultdir + "%s-*direct" % wksname)
            self.assertEqual(1, len(out))

    @OETestID(1851)
    def test_kickstart_parser(self):
        """Test wks parser options"""
        with NamedTemporaryFile("w", suffix=".wks") as wks:
            wks.writelines(['part / --fstype ext3 --source rootfs --system-id 0xFF '\
                            '--overhead-factor 1.2 --size 100k\n'])
            wks.flush()
            cmd = "wic create %s -e core-image-minimal -o %s" % (wks.name, self.resultdir)
            self.assertEqual(0, runCmd(cmd).status)
            wksname = os.path.splitext(os.path.basename(wks.name))[0]
            out = glob(self.resultdir + "%s-*direct" % wksname)
            self.assertEqual(1, len(out))

    @OETestID(1850)
    def test_image_bootpart_globbed(self):
        """Test globbed sources with image-bootpart plugin"""
        img = "core-image-minimal"
        cmd = "wic create sdimage-bootpart -e %s -o %s" % (img, self.resultdir)
        config = 'IMAGE_BOOT_FILES = "%s*"' % get_bb_var('KERNEL_IMAGETYPE', img)
        self.append_config(config)
        self.assertEqual(0, runCmd(cmd).status)
        self.remove_config(config)
        self.assertEqual(1, len(glob(self.resultdir + "sdimage-bootpart-*direct")))

    @OETestID(1855)
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

    @OETestID(1857)
    def test_wic_ls(self):
        """Test listing image content using 'wic ls'"""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir).status)
        images = glob(self.resultdir + "wictestdisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list partitions
        result = runCmd("wic ls %s -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        self.assertEqual(3, len(result.output.split('\n')))

        # list directory content of the first partition
        result = runCmd("wic ls %s:1/ -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        self.assertEqual(6, len(result.output.split('\n')))

    @OETestID(1856)
    def test_wic_cp(self):
        """Test copy files and directories to the the wic image."""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir).status)
        images = glob(self.resultdir + "wictestdisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the first partition
        result = runCmd("wic ls %s:1/ -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        self.assertEqual(6, len(result.output.split('\n')))

        with NamedTemporaryFile("w", suffix=".wic-cp") as testfile:
            testfile.write("test")

            # copy file to the partition
            result = runCmd("wic cp %s %s:1/ -n %s" % (testfile.name, images[0], sysroot))
            self.assertEqual(0, result.status)

            # check if file is there
            result = runCmd("wic ls %s:1/ -n %s" % (images[0], sysroot))
            self.assertEqual(0, result.status)
            self.assertEqual(7, len(result.output.split('\n')))
            self.assertTrue(os.path.basename(testfile.name) in result.output)

            # prepare directory
            testdir = os.path.join(self.resultdir, 'wic-test-cp-dir')
            testsubdir = os.path.join(testdir, 'subdir')
            os.makedirs(os.path.join(testsubdir))
            copy(testfile.name, testdir)

            # copy directory to the partition
            result = runCmd("wic cp %s %s:1/ -n %s" % (testdir, images[0], sysroot))
            self.assertEqual(0, result.status)

            # check if directory is there
            result = runCmd("wic ls %s:1/ -n %s" % (images[0], sysroot))
            self.assertEqual(0, result.status)
            self.assertEqual(8, len(result.output.split('\n')))
            self.assertTrue(os.path.basename(testdir) in result.output)

    @OETestID(1858)
    def test_wic_rm(self):
        """Test removing files and directories from the the wic image."""
        self.assertEqual(0, runCmd("wic create mkefidisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir).status)
        images = glob(self.resultdir + "mkefidisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the first partition
        result = runCmd("wic ls %s:1 -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        self.assertIn('\nBZIMAGE        ', result.output)
        self.assertIn('\nEFI          <DIR>     ', result.output)

        # remove file
        result = runCmd("wic rm %s:1/bzimage -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)

        # remove directory
        result = runCmd("wic rm %s:1/efi -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)

        # check if they're removed
        result = runCmd("wic ls %s:1 -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        self.assertNotIn('\nBZIMAGE        ', result.output)
        self.assertNotIn('\nEFI          <DIR>     ', result.output)

    @OETestID(1922)
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
            self.assertEqual(0, runCmd(cmd).status)
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
            self.assertEqual(0, runCmd(cmd).status)

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
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir).status)
        images = glob(self.resultdir + "wictestdisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the second ext4 partition
        result = runCmd("wic ls %s:2/ -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        self.assertTrue(set(['bin', 'home', 'proc', 'usr', 'var', 'dev', 'lib', 'sbin']).issubset(
                            set(line.split()[-1] for line in result.output.split('\n') if line)))

    def test_wic_cp_ext(self):
        """Test copy files and directories to the ext partition."""
        self.assertEqual(0, runCmd("wic create wictestdisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir).status)
        images = glob(self.resultdir + "wictestdisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the ext4 partition
        result = runCmd("wic ls %s:2/ -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        dirs = set(line.split()[-1] for line in result.output.split('\n') if line)
        self.assertTrue(set(['bin', 'home', 'proc', 'usr', 'var', 'dev', 'lib', 'sbin']).issubset(dirs))

        with NamedTemporaryFile("w", suffix=".wic-cp") as testfile:
            testfile.write("test")

            # copy file to the partition
            result = runCmd("wic cp %s %s:2/ -n %s" % (testfile.name, images[0], sysroot))
            self.assertEqual(0, result.status)

            # check if file is there
            result = runCmd("wic ls %s:2/ -n %s" % (images[0], sysroot))
            self.assertEqual(0, result.status)
            newdirs = set(line.split()[-1] for line in result.output.split('\n') if line)
            self.assertEqual(newdirs.difference(dirs), set([os.path.basename(testfile.name)]))

    def test_wic_rm_ext(self):
        """Test removing files from the ext partition."""
        self.assertEqual(0, runCmd("wic create mkefidisk "
                                   "--image-name=core-image-minimal "
                                   "-D -o %s" % self.resultdir).status)
        images = glob(self.resultdir + "mkefidisk-*.direct")
        self.assertEqual(1, len(images))

        sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'wic-tools')

        # list directory content of the /etc directory on ext4 partition
        result = runCmd("wic ls %s:2/etc/ -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        self.assertTrue('fstab' in [line.split()[-1] for line in result.output.split('\n') if line])

        # remove file
        result = runCmd("wic rm %s:2/etc/fstab -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)

        # check if it's removed
        result = runCmd("wic ls %s:2/etc/ -n %s" % (images[0], sysroot))
        self.assertEqual(0, result.status)
        self.assertTrue('fstab' not in [line.split()[-1] for line in result.output.split('\n') if line])
