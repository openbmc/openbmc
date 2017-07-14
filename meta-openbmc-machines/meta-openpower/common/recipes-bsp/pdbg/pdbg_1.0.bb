SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors from the BMC."
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "git://github.com/open-power/pdbg.git;branch=master"
SRCREV = "7c56ba516efa9ad1b6827ec3a90ac76b8f66f907"

DEPENDS += "dtc-native"

S = "${WORKDIR}/git"

inherit autotools
