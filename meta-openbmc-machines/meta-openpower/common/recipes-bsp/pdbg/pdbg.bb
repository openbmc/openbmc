SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors from the BMC."
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "git://github.com/open-power/pdbg.git"
SRC_URI += "file://0001-CFAM-reg-0x2918-access-hack.patch"

SRCREV = "6554a4a6bf73c22766650977eec07f90502b7438"
PV = "git${SRCREV}"

S = "${WORKDIR}/git"

inherit autotools
