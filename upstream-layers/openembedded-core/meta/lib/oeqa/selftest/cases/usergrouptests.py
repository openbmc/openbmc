#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake
from oeqa.utils.commands import bitbake, get_bb_var, get_test_layer

class UserGroupTests(OESelftestTestCase):
    def test_group_from_dep_package(self):
        self.logger.info("Building creategroup2")
        bitbake(' creategroup2 creategroup1')
        bitbake(' creategroup2 creategroup1 -c clean')
        self.logger.info("Packaging creategroup2")
        self.assertTrue(bitbake(' creategroup2 -c package'))

    def test_add_task_between_p_sysroot_and_package(self):
        # Test for YOCTO #14961
        self.assertTrue(bitbake('useraddbadtask -C fetch'))

    def test_postinst_order(self):
        self.logger.info("Building dcreategroup")
        self.assertTrue(bitbake(' dcreategroup'))

    def test_static_useradd_from_dynamic(self):
        metaselftestpath = get_test_layer()
        self.logger.info("Building core-image-minimal to generate passwd/group file")
        bitbake(' core-image-minimal')
        self.logger.info("Setting up useradd-staticids")
        repropassdir = os.path.join(metaselftestpath, "conf/include")
        os.makedirs(repropassdir)
        etcdir=os.path.join(os.path.join(os.path.join(get_bb_var("TMPDIR"), "work"), \
                            os.path.join(get_bb_var("MACHINE").replace("-","_")+"-poky-linux", "core-image-minimal/1.0/rootfs/etc")))
        shutil.copy(os.path.join(etcdir, "passwd"), os.path.join(repropassdir, "reproducable-passwd"))
        shutil.copy(os.path.join(etcdir, "group"), os.path.join(repropassdir, "reproducable-group"))
        # Copy the original local.conf
        shutil.copyfile(os.path.join(os.environ.get('BUILDDIR'), 'conf/local.conf'), os.path.join(os.environ.get('BUILDDIR'), 'conf/local.conf.orig'))

        self.write_config("USERADDEXTENSION = \"useradd-staticids\"")
        self.write_config("USERADD_ERROR_DYNAMIC ??= \"error\"")
        self.write_config("USERADD_UID_TABLES += \"conf/include/reproducible-passwd\"")
        self.write_config("USERADD_GID_TABLES += \"conf/include/reproducible-group\"")
        self.logger.info("Rebuild with staticids")
        bitbake(' core-image-minimal')
        shutil.copyfile(os.path.join(os.environ.get('BUILDDIR'), 'conf/local.conf.orig'), os.path.join(os.environ.get('BUILDDIR'), 'conf/local.conf'))
        self.logger.info("Rebuild without staticids")
        bitbake(' core-image-minimal')
        self.write_config("USERADDEXTENSION = \"useradd-staticids\"")
        self.write_config("USERADD_ERROR_DYNAMIC ??= \"error\"")
        self.write_config("USERADD_UID_TABLES += \"files/static-passwd\"")
        self.write_config("USERADD_GID_TABLES += \"files/static-group\"")
        self.logger.info("Rebuild with other staticids")
        self.assertTrue(bitbake(' core-image-minimal'))
