SUMMARY     = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors"
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

PV = "2.1+git${SRCPV}"

SRC_URI += "git://github.com/open-power/pdbg.git"
SRCREV = "2463be165d7eaa50b648c343e410d851edfb70ce"

DEPENDS += "dtc-native"

S = "${WORKDIR}/git"

inherit autotools
