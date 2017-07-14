SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors from the BMC."
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "git://github.com/open-power/pdbg.git;branch=master"
SRCREV = "c9001c480c208d4690797edd75d0b5a24a0893c8"

DEPENDS += "dtc-native"

S = "${WORKDIR}/git"

inherit autotools
