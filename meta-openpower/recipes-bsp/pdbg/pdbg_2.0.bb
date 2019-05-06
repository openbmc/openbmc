SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors"
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

PV = "2.0+git${SRCPV}"

SRC_URI += "git://github.com/open-power/pdbg.git"
SRCREV = "854c4c5facff43af9e0fe5d7062b58f631987b0b"

DEPENDS += "dtc-native"

S = "${WORKDIR}/git"

inherit autotools
