SUMMARY = "Phosphor DBus Monitor"
DESCRIPTION = "Phosphor DBus Monitor is a general purpose DBus application \
that watches DBus traffic for events and takes actions based on those events."
HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-monitor"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'phosphor-no-snmp', '', 'snmp', d)}"
PACKAGECONFIG[snmp] = "-Dsnmp=enabled, -Dsnmp=disabled, phosphor-snmp"

DEPENDS += " \
        ${PN}-config \
        phosphor-logging \
        ${PYTHON_PN}-sdbus++-native \
        sdeventplus \
        gtest \
        ${PYTHON_PN}-native \
        ${PYTHON_PN}-pyyaml-native \
        ${PYTHON_PN}-setuptools-native \
        ${PYTHON_PN}-mako-native \
        "
SRCREV = "7c7c2c7670e0001e197c06b11cb38b534c39ee5d"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-dbus-monitor;branch=master;protocol=https"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "phosphor-dbus-monitor.service"

inherit meson \
        pkgconfig \
        python3native \
        phosphor-dbus-monitor \
        obmc-phosphor-systemd

EXTRA_OEMESON = " \
        -DYAML_PATH=${STAGING_DIR_HOST}${config_dir} \
        "
