SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors from the BMC."
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "git://github.com/open-power/pdbg.git"

SRCREV = "e622883810266bcbc09ab5e1bae6a137dcda74c7"
PV = "git${SRCREV}"

S = "${WORKDIR}/git"

inherit autotools
