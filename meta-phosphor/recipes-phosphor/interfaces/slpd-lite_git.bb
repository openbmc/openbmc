SUMMARY = "Lightweight SLP Server"
DESCRIPTION = "Lightweight Unicast-only SLP Server"
HOMEPAGE = "http://github.com/openbmc/slpd-lite"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"
SRCREV = "11c35e133e61f4b31e0cf629d62d3a066b7e2970"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/slpd-lite;branch=master;protocol=https"

SYSTEMD_SERVICE:${PN} += "slpd-lite.service"

inherit meson pkgconfig
inherit obmc-phosphor-systemd
