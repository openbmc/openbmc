SUMMARY = "Phosphor DBus Monitor"
DESCRIPTION = "Phosphor DBus Monitor is a general purpose DBus application \
that watches DBus traffic for events and takes actions based on those events."
PR = "r1"

HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-monitor"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI = "git://github.com/openbmc/phosphor-dbus-monitor"
SRCREV = "2df09e242d6dd5961d831bd179f73489eba526ed"

inherit autotools \
        pkgconfig \
        pythonnative \
        phosphor-dbus-monitor \
        obmc-phosphor-systemd

DEPENDS += " \
        ${PN}-config-native \
        phosphor-logging \
        autoconf-archive-native \
        gtest \
        "
RDEPENDS_${PN} += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        "

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
        YAML_PATH=${STAGING_DIR_NATIVE}${config_dir} \
        "
