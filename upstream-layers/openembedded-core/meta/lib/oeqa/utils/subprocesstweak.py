#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import subprocess

class OETestCalledProcessError(subprocess.CalledProcessError):
    def __str__(self):
        def strify(o):
            return o.decode("utf-8", errors="replace") if isinstance(o, bytes) else o

        s = super().__str__()
        s = s + "\nStandard Output: " + strify(self.output)
        # stderr is not available for check_output method
        if self.stderr != None:
            s = s + "\nStandard Error: " + strify(self.stderr)
        return s

def errors_have_output():
    subprocess.CalledProcessError = OETestCalledProcessError
