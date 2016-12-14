SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors from the BMC."
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "git://github.com/open-power/pdbg.git"

SRCREV = "bc861c9883bd1c40c7a284d8e4f6676b758268e5"
PV = "git${SRCREV}"

S = "${WORKDIR}/git"

inherit autotools
