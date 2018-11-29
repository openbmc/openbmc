SUMMARY = "Phosphor DBus Monitor"
DESCRIPTION = "Phosphor DBus Monitor is a general purpose DBus application \
that watches DBus traffic for events and takes actions based on those events."
PR = "r1"
PV = "1.0+git${SRCPV}"
HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-monitor"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI = "git://github.com/openbmc/phosphor-dbus-monitor"
SRCREV = "ecf8910c014b94c688d08fa856cd22ec365583fa"

inherit autotools \
        pkgconfig \
        pythonnative \
        phosphor-dbus-monitor \
        obmc-phosphor-systemd

PACKAGE_BEFORE_PN = "phosphor-msl-verify"
SYSTEMD_PACKAGES = "${PN} phosphor-msl-verify"
SYSTEMD_SERVICE_phosphor-msl-verify = "phosphor-msl-verify.service"

DEPENDS += " \
        ${PN}-config-native \
        phosphor-logging \
        autoconf-archive-native \
        sdbusplus-native \
        sdeventplus \
        gtest \
        phosphor-snmp \
        "

FILES_phosphor-msl-verify = "${sbindir}/phosphor-msl-verify"

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
        YAML_PATH=${STAGING_DIR_NATIVE}${config_dir} \
        "
