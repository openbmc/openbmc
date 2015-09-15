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

from glob import glob
from shutil import rmtree

from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var

class Wic(oeSelfTest):
    """Wic test class."""

    resultdir = "/var/tmp/wic/build/"

    @classmethod
    def setUpClass(cls):
        """Build wic runtime dependencies."""
        bitbake('syslinux syslinux-native parted-native gptfdisk-native '
                'dosfstools-native mtools-native')
        Wic.image_is_ready = False

    def setUp(self):
        """This code is executed before each test method."""
        if not Wic.image_is_ready:
            # build core-image-minimal with required features
            features = 'IMAGE_FSTYPES += " hddimg"\nMACHINE_FEATURES_append = " efi"\n'
            self.append_config(features)
            bitbake('core-image-minimal')
            # set this class variable to avoid buiding image many times
            Wic.image_is_ready = True

        rmtree(self.resultdir, ignore_errors=True)

    def test01_help(self):
        """Test wic --help"""
        self.assertEqual(0, runCmd('wic --help').status)

    def test02_createhelp(self):
        """Test wic create --help"""
        self.assertEqual(0, runCmd('wic create --help').status)

    def test03_listhelp(self):
        """Test wic list --help"""
        self.assertEqual(0, runCmd('wic list --help').status)

    def test04_build_image_name(self):
        """Test wic create directdisk --image-name core-image-minimal"""
        self.assertEqual(0, runCmd("wic create directdisk "
                                   "--image-name core-image-minimal").status)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-*.direct")))

    def test05_build_artifacts(self):
        """Test wic create directdisk providing all artifacts."""
        vars = dict((var.lower(), get_bb_var(var, 'core-image-minimal')) \
                        for var in ('STAGING_DATADIR', 'DEPLOY_DIR_IMAGE',
                                    'STAGING_DIR_NATIVE', 'IMAGE_ROOTFS'))
        status = runCmd("wic create directdisk "
                        "-b %(staging_datadir)s "
                        "-k %(deploy_dir_image)s "
                        "-n %(staging_dir_native)s "
                        "-r %(image_rootfs)s" % vars).status
        self.assertEqual(0, status)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-*.direct")))

    def test06_gpt_image(self):
        """Test creation of core-image-minimal with gpt table and UUID boot"""
        self.assertEqual(0, runCmd("wic create directdisk-gpt "
                                   "--image-name core-image-minimal").status)
        self.assertEqual(1, len(glob(self.resultdir + "directdisk-*.direct")))

    def test07_unsupported_subcommand(self):
        """Test unsupported subcommand"""
        self.assertEqual(1, runCmd('wic unsupported',
                         ignore_status=True).status)

    def test08_no_command(self):
        """Test wic without command"""
        self.assertEqual(1, runCmd('wic', ignore_status=True).status)

    def test09_help_kickstart(self):
        """Test wic help overview"""
        self.assertEqual(0, runCmd('wic help overview').status)

    def test10_help_plugins(self):
        """Test wic help plugins"""
        self.assertEqual(0, runCmd('wic help plugins').status)

    def test11_help_kickstart(self):
        """Test wic help kickstart"""
        self.assertEqual(0, runCmd('wic help kickstart').status)

    def test12_compress_gzip(self):
        """Test compressing an image with gzip"""
        self.assertEqual(0, runCmd("wic create directdisk "
                                   "--image-name core-image-minimal "
                                   "-c gzip").status)
        self.assertEqual(1, len(glob(self.resultdir + \
                                         "directdisk-*.direct.gz")))

    def test13_compress_gzip(self):
        """Test compressing an image with bzip2"""
        self.assertEqual(0, runCmd("wic create directdisk "
                                   "--image-name core-image-minimal "
                                   "-c bzip2").status)
        self.assertEqual(1, len(glob(self.resultdir + \
                                         "directdisk-*.direct.bz2")))

    def test14_compress_gzip(self):
        """Test compressing an image with xz"""
        self.assertEqual(0, runCmd("wic create directdisk "
                                   "--image-name core-image-minimal "
                                   "-c xz").status)
        self.assertEqual(1, len(glob(self.resultdir + \
                                         "directdisk-*.direct.xz")))

    def test15_wrong_compressor(self):
        """Test how wic breaks if wrong compressor is provided"""
        self.assertEqual(2, runCmd("wic create directdisk "
                                   "--image-name core-image-minimal "
                                   "-c wrong", ignore_status=True).status)

    def test16_rootfs_indirect_recipes(self):
        """Test usage of rootfs plugin with rootfs recipes"""
        wks = "directdisk-multi-rootfs"
        self.assertEqual(0, runCmd("wic create %s "
                                   "--image-name core-image-minimal "
                                   "--rootfs rootfs1=core-image-minimal "
                                   "--rootfs rootfs2=core-image-minimal" \
                                   % wks).status)
        self.assertEqual(1, len(glob(self.resultdir + "%s*.direct" % wks)))

    def test17_rootfs_artifacts(self):
        """Test usage of rootfs plugin with rootfs paths"""
        vars = dict((var.lower(), get_bb_var(var, 'core-image-minimal')) \
                        for var in ('STAGING_DATADIR', 'DEPLOY_DIR_IMAGE',
                                    'STAGING_DIR_NATIVE', 'IMAGE_ROOTFS'))
        vars['wks'] = "directdisk-multi-rootfs"
        status = runCmd("wic create %(wks)s "
                        "-b %(staging_datadir)s "
                        "-k %(deploy_dir_image)s "
                        "-n %(staging_dir_native)s "
                        "--rootfs-dir rootfs1=%(image_rootfs)s "
                        "--rootfs-dir rootfs2=%(image_rootfs)s" \
                        % vars).status
        self.assertEqual(0, status)
        self.assertEqual(1, len(glob(self.resultdir + \
                                     "%(wks)s-*.direct" % vars)))

    def test18_iso_image(self):
        """Test creation of hybrid iso imagewith legacy and EFI boot"""
        self.assertEqual(0, runCmd("wic create mkhybridiso "
                                   "--image-name core-image-minimal").status)
        self.assertEqual(1, len(glob(self.resultdir + "HYBRID_ISO_IMG-*.direct")))
        self.assertEqual(1, len(glob(self.resultdir + "HYBRID_ISO_IMG-*.iso")))

    def test19_image_env(self):
        """Test generation of <image>.env files."""
        image = 'core-image-minimal'
        stdir = get_bb_var('STAGING_DIR_TARGET', image)
        imgdatadir = os.path.join(stdir, 'imgdata')

        basename = get_bb_var('IMAGE_BASENAME', image)
        self.assertEqual(basename, image)
        path = os.path.join(imgdatadir, basename) + '.env'
        self.assertTrue(os.path.isfile(path))

        wicvars = set(get_bb_var('WICVARS', image).split())
        # filter out optional variables
        wicvars = wicvars.difference(('HDDDIR', 'IMAGE_BOOT_FILES',
                                      'INITRD', 'ISODIR'))
        with open(path) as envfile:
            content = dict(line.split("=", 1) for line in envfile)
            # test if variables used by wic present in the .env file
            for var in wicvars:
                self.assertTrue(var in content, "%s is not in .env file" % var)
                self.assertTrue(content[var])

    def test20_wic_image_type(self):
        """Test building wic images by bitbake"""
        self.assertEqual(0, bitbake('wic-image-minimal').status)

        deploy_dir = get_bb_var('DEPLOY_DIR_IMAGE')
        machine = get_bb_var('MACHINE')
        prefix = os.path.join(deploy_dir, 'wic-image-minimal-%s.' % machine)
        # check if we have result image and manifests symlinks
        # pointing to existing files
        for suffix in ('wic.bz2', 'manifest'):
            path = prefix + suffix
            self.assertTrue(os.path.islink(path))
            self.assertTrue(os.path.isfile(os.path.realpath(path)))

    def test21_qemux86_directdisk(self):
        """Test creation of qemux-86-directdisk image"""
        image = "qemux86-directdisk"
        self.assertEqual(0, runCmd("wic create %s -e core-image-minimal" \
                                   % image).status)
        self.assertEqual(1, len(glob(self.resultdir + "%s-*direct" % image)))

    def test22_mkgummidisk(self):
        """Test creation of mkgummidisk image"""
        image = "mkgummidisk"
        self.assertEqual(0, runCmd("wic create %s -e core-image-minimal" \
                                   % image).status)
        self.assertEqual(1, len(glob(self.resultdir + "%s-*direct" % image)))

    def test23_mkefidisk(self):
        """Test creation of mkefidisk image"""
        image = "mkefidisk"
        self.assertEqual(0, runCmd("wic create %s -e core-image-minimal" \
                                   % image).status)
        self.assertEqual(1, len(glob(self.resultdir + "%s-*direct" % image)))
