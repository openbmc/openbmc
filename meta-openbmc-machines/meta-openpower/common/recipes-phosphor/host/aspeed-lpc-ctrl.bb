SUMMARY = "ASPEED LPC Host Interface Control tool"
DESCRIPTION = "Configures the BMC to expose memory regions to the host"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

inherit autotools

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "pnorboot.service"

SRC_URI += "git://github.com/shenki/aspeed-lpc-control"
SRCREV = "ab2012e749daf567049bf157c3bf037adc62c0e8"
