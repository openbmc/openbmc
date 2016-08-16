SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors from the BMC."
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "file://0001-Makefile-Allow-out-of-tree-builds.patch"

SRCREV = "7c795172a4fa4c8413443c63c5fbc31fe00f1be0"
PV = "git${SRCREV}"

S = "${WORKDIR}/git"

inherit autotools
