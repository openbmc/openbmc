SUMMARY = "ASPEED LPC Host Interface Control tool"
DESCRIPTION = "Configures the BMC to expose memory regions to the host"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit obmc-phosphor-systemd

inherit autotools

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "pnorboot.service"

SRC_URI += "git://github.com/shenki/aspeed-lpc-control"
SRCREV = "af42b7ff01e71c0dd4c60214dd46ed487611f36d"
