from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, runqemu
from oeqa.utils.decorators import testcase
from oeqa.utils.sshcontrol import SSHControl
import os
import sys
import logging

class ImageFeatures(oeSelfTest):

    test_user = 'tester'
    root_user = 'root'

    @testcase(1107)
    def test_non_root_user_can_connect_via_ssh_without_password(self):
        """
        Summary: Check if non root user can connect via ssh without password
        Expected: 1. Connection to the image via ssh using root user without providing a password should be allowed.
                  2. Connection to the image via ssh using tester user without providing a password should be allowed.
        Product: oe-core
        Author: Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        features = 'EXTRA_IMAGE_FEATURES = "ssh-server-openssh empty-root-password allow-empty-password"\n'
        features += 'INHERIT += "extrausers"\n'
        features += 'EXTRA_USERS_PARAMS = "useradd -p \'\' {}; usermod -s /bin/sh {};"'.format(self.test_user, self.test_user)
        self.write_config(features)

        # Build a core-image-minimal
        bitbake('core-image-minimal')

        with runqemu("core-image-minimal") as qemu:
            # Attempt to ssh with each user into qemu with empty password
            for user in [self.root_user, self.test_user]:
                ssh = SSHControl(ip=qemu.ip, logfile=qemu.sshlog, user=user)
                status, output = ssh.run("true")
                self.assertEqual(status, 0, 'ssh to user %s failed with %s' % (user, output))

    @testcase(1115)
    def test_all_users_can_connect_via_ssh_without_password(self):
        """
        Summary:     Check if all users can connect via ssh without password
        Expected: 1. Connection to the image via ssh using root user without providing a password should NOT be allowed.
                  2. Connection to the image via ssh using tester user without providing a password should be allowed.
        Product:     oe-core
        Author:      Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        features = 'EXTRA_IMAGE_FEATURES = "ssh-server-openssh allow-empty-password"\n'
        features += 'INHERIT += "extrausers"\n'
        features += 'EXTRA_USERS_PARAMS = "useradd -p \'\' {}; usermod -s /bin/sh {};"'.format(self.test_user, self.test_user)
        self.write_config(features)

        # Build a core-image-minimal
        bitbake('core-image-minimal')

        with runqemu("core-image-minimal") as qemu:
            # Attempt to ssh with each user into qemu with empty password
            for user in [self.root_user, self.test_user]:
                ssh = SSHControl(ip=qemu.ip, logfile=qemu.sshlog, user=user)
                status, output = ssh.run("true")
                if user == 'root':
                    self.assertNotEqual(status, 0, 'ssh to user root was allowed when it should not have been')
                else:
                    self.assertEqual(status, 0, 'ssh to user tester failed with %s' % output)


    @testcase(1116)
    def test_clutter_image_can_be_built(self):
        """
        Summary:     Check if clutter image can be built
        Expected:    1. core-image-clutter can be built
        Product:     oe-core
        Author:      Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        # Build a core-image-clutter
        bitbake('core-image-clutter')

    @testcase(1117)
    def test_wayland_support_in_image(self):
        """
        Summary:     Check Wayland support in image
        Expected:    1. Wayland image can be build
                     2. Wayland feature can be installed
        Product:     oe-core
        Author:      Ionut Chisanovici <ionutx.chisanovici@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        features = 'DISTRO_FEATURES_append = " wayland"\n'
        features += 'CORE_IMAGE_EXTRA_INSTALL += "wayland weston"'
        self.write_config(features)

        # Build a core-image-weston
        bitbake('core-image-weston')

