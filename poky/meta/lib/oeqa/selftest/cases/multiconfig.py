#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import textwrap
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class MultiConfig(OESelftestTestCase):

    def test_multiconfig(self):
        """
        Test that a simple multiconfig build works. This uses the mcextend class and the
        multiconfig-image-packager test recipe to build a core-image-full-cmdline image which
        contains a tiny core-image-minimal and a musl core-image-minimal, installed as packages.
        """

        config = """
IMAGE_INSTALL:append:pn-core-image-full-cmdline = " multiconfig-image-packager-tiny multiconfig-image-packager-musl"
BBMULTICONFIG = "tiny musl"
"""
        self.write_config(config)

        muslconfig = """
MACHINE = "qemux86-64"
DISTRO = "poky"
TCLIBC = "musl"
TMPDIR = "${TOPDIR}/tmp-mc-musl"
"""
        self.write_config(muslconfig, 'musl')

        tinyconfig = """
MACHINE = "qemux86"
DISTRO = "poky-tiny"
TMPDIR = "${TOPDIR}/tmp-mc-tiny"
"""
        self.write_config(tinyconfig, 'tiny')

        # Build a core-image-minimal
        bitbake('core-image-full-cmdline')

    def test_multiconfig_reparse(self):
        """
        Test that changes to a multiconfig conf file are correctly detected and
        cause a reparse/rebuild of a recipe.
        """
        config = textwrap.dedent('''\
                MCTESTVAR = "test"
                BBMULTICONFIG = "test"
                ''')
        self.write_config(config)

        testconfig = textwrap.dedent('''\
                MCTESTVAR:append = "1"
                ''')
        self.write_config(testconfig, 'test')

        # Check that the 1) the task executed and 2) that it output the correct
        # value. Note "bitbake -e" is not used because it always reparses the
        # recipe and we want to ensure that the automatic reparsing and parse
        # caching is detected.
        result = bitbake('mc:test:multiconfig-test-parse -c showvar')
        self.assertIn('MCTESTVAR=test1', result.output.splitlines())

        testconfig = textwrap.dedent('''\
                MCTESTVAR:append = "2"
                ''')
        self.write_config(testconfig, 'test')

        result = bitbake('mc:test:multiconfig-test-parse -c showvar')
        self.assertIn('MCTESTVAR=test2', result.output.splitlines())

    def test_multiconfig_inlayer(self):
        """
        Test that a multiconfig from meta-selftest works.
        """

        config = """
BBMULTICONFIG = "muslmc"
"""
        self.write_config(config)

        # Build a core-image-minimal, only dry run needed to check config is present
        bitbake('mc:muslmc:bash -n')
