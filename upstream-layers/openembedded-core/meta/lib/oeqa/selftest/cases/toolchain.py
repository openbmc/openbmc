#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import shutil
import subprocess
import tempfile
from types import SimpleNamespace

import oe.path
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, get_bb_vars

class ToolchainTests(OESelftestTestCase):

    def test_toolchain_switching(self):
        """
        Test that a configuration that uses GCC by default but clang for one
        specific recipe does infact do that.
        """

        def extract_comment(objcopy, filename):
            """
            Using the specified `objcopy`, return the .comment segment from
            `filename` as a bytes().
            """
            with tempfile.NamedTemporaryFile(prefix="comment-") as f:
                cmd = [objcopy, "--dump-section", ".comment=" + f.name, filename]
                subprocess.run(cmd, check=True)
                # clang's objcopy writes to a temporary file and renames, so we need to re-open.
                with open(f.name, "rb") as f2:
                    return f2.read()

        def check_recipe(recipe, filename, override, comment_present, comment_absent=None):
            """
            Check that `filename` in `recipe`'s bindir contains `comment`, and
            the overrides contain `override`.
            """
            d = SimpleNamespace(**get_bb_vars(("D", "bindir", "OBJCOPY", "OVERRIDES", "PATH"), target=recipe))

            self.assertIn(override, d.OVERRIDES)

            binary = oe.path.join(d.D, d.bindir, filename)

            objcopy = shutil.which(d.OBJCOPY, path=d.PATH)
            self.assertIsNotNone(objcopy)

            comment = extract_comment(objcopy, binary)
            self.assertIn(comment_present, comment)
            if comment_absent:
                self.assertNotIn(comment_absent, comment)


        # GCC by default, clang for selftest-hello.
        self.write_config("""
TOOLCHAIN = "gcc"
TOOLCHAIN:pn-selftest-hello = "clang"
        """)

        # Force these recipes to re-install so we can extract the .comments from
        # the install directory, as they're stripped out of the final packages.
        bitbake("m4 selftest-hello -C install")

        # m4 should be built with GCC and only GCC
        check_recipe("m4", "m4", "toolchain-gcc", b"GCC: (GNU)", b"clang")

        # helloworld should be built with clang. We can't assert that GCC is not
        # present as it will be linked against glibc which is built with GCC.
        check_recipe("selftest-hello", "helloworld", "toolchain-clang", b"clang version")
