SUMMARY = "ASPEED LPC Host Interface Control tool"
DESCRIPTION = "Configures the BMC to expose memory regions to the host"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

inherit autotools

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "pnorboot.service"

SRC_URI += "git://github.com/shenki/aspeed-lpc-control"
SRCREV = "af42b7ff01e71c0dd4c60214dd46ed487611f36d"
