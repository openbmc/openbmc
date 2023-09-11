#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import tempfile
import textwrap
import bb.tinfoil
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
MIRRORS:forcevariable = ""
PREMIRRORS:forcevariable = ""
""" % dldir
        self.write_config(features)
        oe.path.remove(dldir, recurse=True)
        bitbake("dbus-wait -c fetch -f")

        # No mirrors and broken git, should fail
        features = """
DL_DIR = "%s"
SRC_URI:pn-dbus-wait = "git://git.yoctoproject.org/dbus-wait;branch=master;protocol=git"
GIT_PROXY_COMMAND = "false"
MIRRORS:forcevariable = ""
PREMIRRORS:forcevariable = ""
""" % dldir
        self.write_config(features)
        oe.path.remove(dldir, recurse=True)
        with self.assertRaises(AssertionError):
            bitbake("dbus-wait -c fetch -f")

        # Broken git but a specific mirror
        features = """
DL_DIR = "%s"
SRC_URI:pn-dbus-wait = "git://git.yoctoproject.org/dbus-wait;branch=master;protocol=git"
GIT_PROXY_COMMAND = "false"
MIRRORS:forcevariable = "git://.*/.* http://downloads.yoctoproject.org/mirror/sources/"
""" % dldir
        self.write_config(features)
        oe.path.remove(dldir, recurse=True)
        bitbake("dbus-wait -c fetch -f")


class Dependencies(OESelftestTestCase):
    def write_recipe(self, content, tempdir):
        f = os.path.join(tempdir, "test.bb")
        with open(f, "w") as fd:
            fd.write(content)
        return f

    def test_dependencies(self):
        """
        Verify that the correct dependencies are generated for specific SRC_URI entries.
        """

        with bb.tinfoil.Tinfoil() as tinfoil, tempfile.TemporaryDirectory(prefix="selftest-fetch") as tempdir:
            tinfoil.prepare(config_only=False, quiet=2)

            r = """
            LICENSE="CLOSED"
            SRC_URI="http://example.com/tarball.zip"
            """
            f = self.write_recipe(textwrap.dedent(r), tempdir)
            d = tinfoil.parse_recipe_file(f)
            self.assertIn("wget-native", d.getVarFlag("do_fetch", "depends"))
            self.assertIn("unzip-native", d.getVarFlag("do_unpack", "depends"))

            # Verify that the downloadfilename overrides the URI
            r = """
            LICENSE="CLOSED"
            SRC_URI="https://example.com/tarball;downloadfilename=something.zip"
            """
            f = self.write_recipe(textwrap.dedent(r), tempdir)
            d = tinfoil.parse_recipe_file(f)
            self.assertIn("wget-native", d.getVarFlag("do_fetch", "depends"))
            self.assertIn("unzip-native", d.getVarFlag("do_unpack", "depends") or "")

            r = """
            LICENSE="CLOSED"
            SRC_URI="ftp://example.com/tarball.lz"
            """
            f = self.write_recipe(textwrap.dedent(r), tempdir)
            d = tinfoil.parse_recipe_file(f)
            self.assertIn("wget-native", d.getVarFlag("do_fetch", "depends"))
            self.assertIn("lzip-native", d.getVarFlag("do_unpack", "depends"))

            r = """
            LICENSE="CLOSED"
            SRC_URI="git://example.com/repo;branch=master;rev=ffffffffffffffffffffffffffffffffffffffff"
            """
            f = self.write_recipe(textwrap.dedent(r), tempdir)
            d = tinfoil.parse_recipe_file(f)
            self.assertIn("git-native", d.getVarFlag("do_fetch", "depends"))
