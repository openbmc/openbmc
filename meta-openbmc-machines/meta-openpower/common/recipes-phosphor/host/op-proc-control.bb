SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native phosphor-logging"
RDEPENDS_${PN} += "phosphor-logging"

SRC_URI += "git://github.com/openbmc/openpower-proc-control"
SRCREV = "8316b77b67aac9dea3e7281e2455ad80f509a5b3"
