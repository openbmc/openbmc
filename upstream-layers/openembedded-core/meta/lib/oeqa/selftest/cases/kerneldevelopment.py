#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, get_bb_var
from oeqa.utils.git import GitRepo

class KernelDev(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(KernelDev, cls).setUpClass()
        # Create the recipe directory structure inside the created layer
        cls.layername = 'meta-kerneltest'
        runCmd('bitbake-layers create-layer %s' % cls.layername)
        runCmd('mkdir -p %s/recipes-kernel/linux/linux-yocto' % cls.layername)
        cls.recipes_linuxyocto_dir = os.path.join \
            (cls.builddir, cls.layername, 'recipes-kernel', 'linux', 'linux-yocto')
        cls.recipeskernel_dir = os.path.dirname(cls.recipes_linuxyocto_dir)
        runCmd('bitbake-layers add-layer %s' % cls.layername)

    @classmethod
    def tearDownClass(cls):
        runCmd('bitbake-layers remove-layer %s' % cls.layername, ignore_status=True)
        runCmd('rm -rf %s' % cls.layername)
        super(KernelDev, cls).tearDownClass()

    def setUp(self):
        super(KernelDev, self).setUp()
        self.set_machine_config('MACHINE = "qemux86-64"\n')

    def test_apply_patches(self):
        """
        Summary:     Able to apply a single patch to the Linux kernel source
        Expected:    The README file should exist and the patch changes should be
                     displayed at the end of the file.
        Product:     Kernel Development
        Author:      Yeoh Ee Peng <ee.peng.yeoh@intel.com>
        AutomatedBy: Mazliana Mohamad <mazliana.mohamad@intel.com>
        """
        runCmd('bitbake virtual/kernel -c patch')
        kernel_source = get_bb_var('STAGING_KERNEL_DIR')
        readme = os.path.join(kernel_source, 'README')

        # This test step adds modified file 'README' to git and creates a
        # patch file '0001-KERNEL_DEV_TEST_CASE.patch' at the same location as file
        patch_content = 'This is a test to apply a patch to the kernel'
        with open(readme, 'a+') as f:
            f.write(patch_content)
        repo = GitRepo('%s' % kernel_source, is_topdir=True)
        repo.run_cmd('add %s' % readme)
        repo.run_cmd(['commit', '-m', 'KERNEL_DEV_TEST_CASE'])
        repo.run_cmd(['format-patch', '-1'])
        patch_name = '0001-KERNEL_DEV_TEST_CASE.patch'
        patchpath = os.path.join(kernel_source, patch_name)
        runCmd('mv %s %s' % (patchpath, self.recipes_linuxyocto_dir))
        runCmd('rm %s ' % readme)
        self.assertFalse(os.path.exists(readme))

        recipe_append = os.path.join(self.recipeskernel_dir, 'linux-yocto_%.bbappend')
        with open(recipe_append, 'w+') as fh:
            fh.write('SRC_URI += "file://%s"\n' % patch_name)
            fh.write('ERROR_QA:remove:pn-linux-yocto = "patch-status"\n')
            fh.write('FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"')

        runCmd('bitbake virtual/kernel -c clean')
        runCmd('bitbake virtual/kernel -c patch')
        self.assertTrue(os.path.exists(readme))
        result = runCmd('tail -n 1 %s' % readme)
        self.assertEqual(result.output, patch_content)
