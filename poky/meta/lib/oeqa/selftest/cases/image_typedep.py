#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class ImageTypeDepTests(OESelftestTestCase):

    # Verify that when specifying a IMAGE_TYPEDEP: of the form "foo.bar" that
    # the conversion type bar gets added as a dep as well
    def test_conversion_typedep_added(self):

        self.write_recipeinc('emptytest', """
# Try to empty out the default dependency list
PACKAGE_INSTALL = ""
DISTRO_EXTRA_RDEPENDS=""

LICENSE = "MIT"
IMAGE_FSTYPES = "testfstype"

IMAGE_TYPES_MASKED += "testfstype"
IMAGE_TYPEDEP:testfstype = "tar.bz2"

inherit image

""")
        # First get the dependency that should exist for bz2, it will look
        # like CONVERSION_DEPENDS_bz2="somedep"
        result = bitbake('-e emptytest')

        dep = None
        for line in result.output.split('\n'):
            if line.startswith('CONVERSION_DEPENDS_bz2'):
                dep = line.split('=')[1].strip('"')
                break

        self.assertIsNotNone(dep, "CONVERSION_DEPENDS_bz2 dependency not found in bitbake -e output")

        # Now get the dependency task list and check for the expected task
        # dependency
        bitbake('-g emptytest')

        taskdependsfile = os.path.join(self.builddir, 'task-depends.dot')
        dep =  dep + ".do_populate_sysroot"
        depfound = False
        expectedline = '"emptytest.do_rootfs" -> "{}"'.format(dep)

        with open(taskdependsfile, "r") as f:
            for line in f:
                if line.strip() == expectedline:
                    depfound = True
                    break

        if not depfound:
            raise AssertionError("\"{}\" not found".format(expectedline))
