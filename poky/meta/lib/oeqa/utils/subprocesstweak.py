#
# SPDX-License-Identifier: MIT
#
import subprocess

class OETestCalledProcessError(subprocess.CalledProcessError):
    def __str__(self):
        def strify(o):
            if isinstance(o, bytes):
                return o.decode("utf-8", errors="replace")
            else:
                return o

        s = "Command '%s' returned non-zero exit status %d" % (self.cmd, self.returncode)
        if hasattr(self, "output") and self.output:
            s = s + "\nStandard Output: " + strify(self.output)
        if hasattr(self, "stderr") and self.stderr:
            s = s + "\nStandard Error: " + strify(self.stderr)
        return s

def errors_have_output():
    subprocess.CalledProcessError = OETestCalledProcessError
