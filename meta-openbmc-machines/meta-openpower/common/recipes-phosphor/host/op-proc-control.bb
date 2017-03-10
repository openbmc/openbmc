SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native phosphor-logging"

SRC_URI += "git:///home/spinler/openbmc/openpower-proc-control"
SRCREV = "8bd1abf0af16fc67c2d9abc021ee92618d5d53e4"
