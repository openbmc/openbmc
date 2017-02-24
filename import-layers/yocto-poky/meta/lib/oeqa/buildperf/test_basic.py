# Copyright (c) 2016, Intel Corporation.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms and conditions of the GNU General Public License,
# version 2, as published by the Free Software Foundation.
#
# This program is distributed in the hope it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.
#
"""Basic set of build performance tests"""
import os
import shutil

import oe.path
from oeqa.buildperf import BuildPerfTestCase
from oeqa.utils.commands import get_bb_vars


class Test1P1(BuildPerfTestCase):
    build_target = 'core-image-sato'

    def test1(self):
        """Measure wall clock of bitbake core-image-sato and size of tmp dir"""
        self.rm_tmp()
        self.rm_sstate()
        self.rm_cache()
        self.sync()
        self.measure_cmd_resources(['bitbake', self.build_target], 'build',
                                   'bitbake ' + self.build_target, save_bs=True)
        self.measure_disk_usage(self.bb_vars['TMPDIR'], 'tmpdir', 'tmpdir')


class Test1P2(BuildPerfTestCase):
    build_target = 'virtual/kernel'

    def test12(self):
        """Measure bitbake virtual/kernel"""
        # Build and cleans state in order to get all dependencies pre-built
        self.log_cmd_output(['bitbake', self.build_target])
        self.log_cmd_output(['bitbake', self.build_target, '-c', 'cleansstate'])

        self.sync()
        self.measure_cmd_resources(['bitbake', self.build_target], 'build',
                                   'bitbake ' + self.build_target)


class Test1P3(BuildPerfTestCase):
    build_target = 'core-image-sato'

    def test13(self):
        """Build core-image-sato with rm_work enabled"""
        postfile = os.path.join(self.out_dir, 'postfile.conf')
        with open(postfile, 'w') as fobj:
            fobj.write('INHERIT += "rm_work"\n')
        try:
            self.rm_tmp()
            self.rm_sstate()
            self.rm_cache()
            self.sync()
            cmd = ['bitbake', '-R', postfile, self.build_target]
            self.measure_cmd_resources(cmd, 'build',
                                       'bitbake' + self.build_target,
                                       save_bs=True)
            self.measure_disk_usage(self.bb_vars['TMPDIR'], 'tmpdir', 'tmpdir')
        finally:
            os.unlink(postfile)


class Test2(BuildPerfTestCase):
    build_target = 'core-image-sato'

    def test2(self):
        """Measure bitbake core-image-sato -c rootfs with sstate"""
        # Build once in order to populate sstate cache
        self.log_cmd_output(['bitbake', self.build_target])

        self.rm_tmp()
        self.rm_cache()
        self.sync()
        cmd = ['bitbake', self.build_target, '-c', 'rootfs']
        self.measure_cmd_resources(cmd, 'do_rootfs', 'bitbake do_rootfs')


class Test3(BuildPerfTestCase):

    def test3(self):
        """Parsing time metrics (bitbake -p)"""
        # Drop all caches and parse
        self.rm_cache()
        oe.path.remove(os.path.join(self.bb_vars['TMPDIR'], 'cache'), True)
        self.measure_cmd_resources(['bitbake', '-p'], 'parse_1',
                                   'bitbake -p (no caches)')
        # Drop tmp/cache
        oe.path.remove(os.path.join(self.bb_vars['TMPDIR'], 'cache'), True)
        self.measure_cmd_resources(['bitbake', '-p'], 'parse_2',
                                   'bitbake -p (no tmp/cache)')
        # Parse with fully cached data
        self.measure_cmd_resources(['bitbake', '-p'], 'parse_3',
                                   'bitbake -p (cached)')


class Test4(BuildPerfTestCase):
    build_target = 'core-image-sato'

    def test4(self):
        """eSDK metrics"""
        self.log_cmd_output("bitbake {} -c do_populate_sdk_ext".format(
            self.build_target))
        self.bb_vars = get_bb_vars(None, self.build_target)
        tmp_dir = self.bb_vars['TMPDIR']
        installer = os.path.join(
            self.bb_vars['SDK_DEPLOY'],
            self.bb_vars['TOOLCHAINEXT_OUTPUTNAME'] + '.sh')
        # Measure installer size
        self.measure_disk_usage(installer, 'installer_bin', 'eSDK installer',
                                apparent_size=True)
        # Measure deployment time and deployed size
        deploy_dir = os.path.join(tmp_dir, 'esdk-deploy')
        if os.path.exists(deploy_dir):
            shutil.rmtree(deploy_dir)
        self.sync()
        self.measure_cmd_resources([installer, '-y', '-d', deploy_dir],
                                   'deploy', 'eSDK deploy')
        self.measure_disk_usage(deploy_dir, 'deploy_dir', 'deploy dir',
                                apparent_size=True)
