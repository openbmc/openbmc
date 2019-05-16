#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import subprocess

from oeqa.core.case import OETestCase

class OESDKTestCase(OETestCase):
    def _run(self, cmd):
        return subprocess.check_output(". %s > /dev/null; %s;" % \
                (self.tc.sdk_env, cmd), shell=True, executable="/bin/bash",
                stderr=subprocess.STDOUT, universal_newlines=True)

    def fetch(self, workdir, dl_dir, url, archive=None):
        if not archive:
            from urllib.parse import urlparse
            archive = os.path.basename(urlparse(url).path)

        if dl_dir:
            tarball = os.path.join(dl_dir, archive)
            if os.path.exists(tarball):
                return tarball

        tarball = os.path.join(workdir, archive)
        subprocess.check_output(["wget", "-O", tarball, url])
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
