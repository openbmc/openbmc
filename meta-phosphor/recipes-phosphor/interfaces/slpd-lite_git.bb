SUMMARY = "Lightweight SLP Server"
DESCRIPTION = "Lightweight Unicast-only SLP Server"
HOMEPAGE = "http://github.com/openbmc/slpd-lite"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"
SRCREV = "8375b7842d5a06cbb7d87abf2dbdd6b56d48c693"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/slpd-lite;branch=master;protocol=https"

SYSTEMD_SERVICE:${PN} += "slpd-lite.service"
S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-systemd
