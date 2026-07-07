SUMMARY = "SPDM Stack"
DESCRIPTION = "Implementation of the SPDM specification for secure authentication and attestation"

SRC_URI = "git://github.com/openbmc/spdm.git;branch=main;protocol=https"
SRCREV = "604128873c749808ef56710d8dc9d3e43cdeb04a"

PV = "1.0+git${SRCPV}"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit meson pkgconfig systemd

DEPENDS = " \
    systemd \
    sdbusplus \
    phosphor-dbus-interfaces \
    libspdm \
    phosphor-logging \
"
SYSTEMD_SERVICE:${PN} += "spdmd.service"

PACKAGECONFIG ?= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'ptest', '', d)} \
"

PACKAGECONFIG[ptest] = "-Dtests=enabled,-Dtests=disabled,gtest"

