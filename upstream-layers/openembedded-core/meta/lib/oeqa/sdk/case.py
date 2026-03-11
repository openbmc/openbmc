#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import subprocess
import shutil
import unittest

from oeqa.core.case import OETestCase
from oeqa.sdkext.context import OESDKExtTestContext

class OESDKTestCase(OETestCase):
    def _run(self, cmd):
        return subprocess.check_output(". %s > /dev/null; %s;" % \
                (self.tc.sdk_env, cmd), shell=True, executable="/bin/bash",
                stderr=subprocess.STDOUT, universal_newlines=True)

    def ensure_host_package(self, *packages, recipe=None):
        """
        Check that the host variation of one of the packages listed is available
        in the SDK (nativesdk-foo for SDK, foo-native for eSDK). The package is
        a list for the case where debian-renaming may have occured, and the
        manifest could contain 'foo' or 'libfoo'.

        If testing an eSDK and the package is not found, then try to install the
        specified recipe to install it from sstate.
        """

        # In a SDK the manifest is correct. In an eSDK the manifest may be
        # correct (type=full) or not include packages that exist in sstate but
        # not installed yet (minimal) so we should try to install the recipe.
        for package in packages:
            if isinstance(self.tc, OESDKExtTestContext):
                package = package + "-native"
            else:
                package = "nativesdk-" + package

            if self.tc.hasHostPackage(package):
                break
        else:
            if isinstance(self.tc, OESDKExtTestContext):
                recipe = (recipe or packages[0]) + "-native"
                print("Trying to install %s..." % recipe)
                self._run('devtool sdk-install %s' % recipe)
            else:
                raise unittest.SkipTest("Test %s needs one of %s" % (self.id(), ", ".join(packages)))

    def ensure_target_package(self, *packages, multilib=False, recipe=None):
        """
        Check that at least one of the packages listed is available in the SDK,
        adding the multilib prefix if required. The target package is a list for
        the case where debian-renaming may have occured, and the manifest could
        contain 'foo' or 'libfoo'.

        If testing an eSDK and the package is not found, then try to install the
        specified recipe to install it from sstate.
        """

        # In a SDK the manifest is correct. In an eSDK the manifest may be
        # correct (type=full) or not include packages that exist in sstate but
        # not installed yet (minimal) so we should try to install the recipe.
        for package in packages:
            if self.tc.hasTargetPackage(package, multilib=multilib):
                break
        else:
            if isinstance(self.tc, OESDKExtTestContext):
                recipe = recipe or packages[0]
                print("Trying to install %s..." % recipe)
                self._run('devtool sdk-install %s' % recipe)
            else:
                raise unittest.SkipTest("Test %s needs one of %s" % (self.id(), ", ".join(packages)))


    def fetch(self, workdir, dl_dir, url, archive=None):
        if not archive:
            from urllib.parse import urlparse
            archive = os.path.basename(urlparse(url).path)

        if dl_dir:
            archive_tarball = os.path.join(dl_dir, archive)
            if os.path.exists(archive_tarball):
                return archive_tarball

        tarball = os.path.join(workdir, archive)
        subprocess.check_output(["wget", "-O", tarball, url], stderr=subprocess.STDOUT)
        if dl_dir and not os.path.exists(archive_tarball):
            shutil.copyfile(tarball, archive_tarball)
        return tarball

    def check_elf(self, path, target_os=None, target_arch=None):
        """
        Verify that the ELF binary $path matches the specified target
        OS/architecture, or if not specified the currently configured MACHINE's
        OS/architecture.
        """
        import oe.qa, oe.elf

        if not target_os or not target_arch:
            output = self._run("echo $OECORE_TARGET_OS:$OECORE_TARGET_ARCH")
            target_os, target_arch = output.strip().split(":")

        machine_data = oe.elf.machine_dict(None)[target_os][target_arch]
        (machine, osabi, abiversion, endian, bits) = machine_data

        elf = oe.qa.ELFFile(path)
        elf.open()

        self.assertEqual(machine, elf.machine(),
                         "Binary was %s but expected %s" %
                         (oe.qa.elf_machine_to_string(elf.machine()), oe.qa.elf_machine_to_string(machine)))
        self.assertEqual(bits, elf.abiSize())
        self.assertEqual(endian, elf.isLittleEndian())
