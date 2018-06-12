SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit autotools pkgconfig pythonnative

SRC_URI += "git://github.com/openbmc/openpower-proc-control"
SRCREV = "6d83ddf7691fed618b8d9e871f608b8754e2134e"

DEPENDS += " \
        autoconf-archive-native \
        phosphor-logging \
        phosphor-dbus-interfaces \
        openpower-dbus-interfaces \
        "

RDEPENDS_${PN} += " \
        phosphor-logging \
        phosphor-dbus-interfaces \
        openpower-dbus-interfaces \
        "
