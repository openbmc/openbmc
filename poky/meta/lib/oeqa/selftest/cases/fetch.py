#
# SPDX-License-Identifier: MIT
#

import oe.path
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class Fetch(OESelftestTestCase):
    def test_git_mirrors(self):
        """
        Verify that the git fetcher will fall back to the HTTP mirrors. The
        recipe needs to be one that we have on the Yocto Project source mirror
        and is hosted in git.
        """

        # TODO: mktempd instead of hardcoding
        dldir = os.path.join(self.builddir, "download-git-mirrors")
        self.track_for_cleanup(dldir)

        # No mirrors, should use git to fetch successfully
        features = """
DL_DIR = "%s"
MIRRORS_forcevariable = ""
PREMIRRORS_forcevariable = ""
""" % dldir
        self.write_config(features)
        oe.path.remove(dldir, recurse=True)
        bitbake("dbus-wait -c fetch -f")

        # No mirrors and broken git, should fail
        features = """
DL_DIR = "%s"
GIT_PROXY_COMMAND = "false"
MIRRORS_forcevariable = ""
PREMIRRORS_forcevariable = ""
""" % dldir
        self.write_config(features)
        oe.path.remove(dldir, recurse=True)
        with self.assertRaises(AssertionError):
            bitbake("dbus-wait -c fetch -f")

        # Broken git but a specific mirror
        features = """
DL_DIR = "%s"
GIT_PROXY_COMMAND = "false"
MIRRORS_forcevariable = "git://.*/.* http://downloads.yoctoproject.org/mirror/sources/"
""" % dldir
        self.write_config(features)
        oe.path.remove(dldir, recurse=True)
        bitbake("dbus-wait -c fetch -f")
