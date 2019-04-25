import os
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake
import oeqa.utils.ftools as ftools

class MultiConfig(OESelftestTestCase):

    def test_multiconfig(self):
        """
        Test that a simple multiconfig build works. This uses the mcextend class and the 
        multiconfig-image-packager test recipe to build a core-image-full-cmdline image which 
        contains a tiny core-image-minimal and a musl core-image-minimal, installed as packages.
        """

        config = """
IMAGE_INSTALL_append_pn-core-image-full-cmdline = " multiconfig-image-packager-tiny multiconfig-image-packager-musl"
BBMULTICONFIG = "tiny musl"
"""
        self.write_config(config)

        muslconfig = """
MACHINE = "qemux86-64"
DISTRO = "poky"
TCLIBC = "musl"
TMPDIR = "${TOPDIR}/tmp-mc-musl"
"""

        tinyconfig = """
MACHINE = "qemux86"
DISTRO = "poky-tiny"
TMPDIR = "${TOPDIR}/tmp-mc-tiny"
"""

        multiconfigdir = self.builddir + "/conf/multiconfig"
        os.makedirs(multiconfigdir, exist_ok=True)
        self.track_for_cleanup(multiconfigdir + "/musl.conf")
        ftools.write_file(multiconfigdir + "/musl.conf", muslconfig)
        self.track_for_cleanup(multiconfigdir + "/tiny.conf")
        ftools.write_file(multiconfigdir + "/tiny.conf", tinyconfig)

        # Build a core-image-minimal
        bitbake('core-image-full-cmdline')

