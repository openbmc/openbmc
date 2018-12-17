import os
import re
import glob as g
import shutil
import tempfile
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars

class MetaOESourceMirroring(OESelftestTestCase):
    # Can we download everything from the OpenEmbedded Sources Mirror over http only
    def test_oe_source_mirror(self):
        self.write_config("""
BB_ALLOWED_NETWORKS = " sources.openembedded.org"
MIRRORS = ""
DL_DIR = "${TMPDIR}/test_oe_downloads"
PREMIRRORS = "\\
    bzr://.*/.*   http://sources.openembedded.org/ \\n \\
    cvs://.*/.*   http://sources.openembedded.org/ \\n \\
    git://.*/.*   http://sources.openembedded.org/ \\n \\
    gitsm://.*/.* http://sources.openembedded.org/ \\n \\
    hg://.*/.*    http://sources.openembedded.org/ \\n \\
    osc://.*/.*   http://sources.openembedded.org/ \\n \\
    p4://.*/.*    http://sources.openembedded.org/ \\n \\
    svn://.*/.*   http://sources.openembedded.org/ \\n \\
    ftp://.*/.*      http://sources.openembedded.org/ \\n \\
    http://.*/.*     http://sources.openembedded.org/ \\n \\
    https://.*/.*    http://sources.openembedded.org/ \\n"
    """)

        bitbake("world --runall fetch")
