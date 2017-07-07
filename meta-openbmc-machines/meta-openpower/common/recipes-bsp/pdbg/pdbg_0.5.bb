SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors from the BMC."
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "git://github.com/open-power/pdbg.git;branch=pdbg-0.x"
SRCREV = "ea3d30854dbaa9aca83e2cce953169e91d2ca5f4"

S = "${WORKDIR}/git"

inherit autotools
