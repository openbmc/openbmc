SUMMARY = "Lightweight SLP Server"
DESCRIPTION = "Lightweight Unicast-only SLP Server"
HOMEPAGE = "http://github.com/openbmc/slpd-lite"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-systemd

SYSTEMD_SERVICE_${PN} += "slpd-lite.service"

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"

SRC_URI += "git://github.com/openbmc/slpd-lite"

SRCREV = "a592888328e79f0ba61a7099fcb1143bc20a0d43"

S = "${WORKDIR}/git"
