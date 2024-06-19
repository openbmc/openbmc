#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.oetimeout import OETimeout

class FtpmTestSuite(OERuntimeTestCase):
    """
    Minimal test for optee-ftpm and ftpm kernel driver interfaces
    """
    @OETimeout(360)
    def test_ftpm(self):
        # device files, need tee-supplicant fully initialized which takes some time
        # and tests seem to run before boot is complete
        cmd = "ls -l /dev/tpm0 /dev/tpmrm0 || ( runlevel; sleep 60; ls -l /dev/tpm0 /dev/tpmrm0 )"
        status, output = self.target.run(cmd, timeout=90)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

        # tpm version
        cmd = "cat /sys/class/tpm/tpm0/tpm_version_major"
        status, output = self.target.run(cmd, timeout=60)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
        self.assertEqual(output, "2", msg='\n'.join([cmd, output]))

        # sha384 pcrs
        cmd = 'for c in $(seq 0 23); do cat /sys/class/tpm/tpm0/pcr-sha384/"${c}"; done'
        status, output = self.target.run(cmd, timeout=60)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

        # sha256 pcrs
        cmd = 'for c in $(seq 0 23); do cat /sys/class/tpm/tpm0/pcr-sha256/"${c}"; done'
        status, output = self.target.run(cmd, timeout=60)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))

        # sha1 pcrs
        cmd = 'for c in $(seq 0 23); do cat /sys/class/tpm/tpm0/pcr-sha1/"${c}"; done'
        status, output = self.target.run(cmd, timeout=60)
        self.assertEqual(status, 0, msg='\n'.join([cmd, output]))
