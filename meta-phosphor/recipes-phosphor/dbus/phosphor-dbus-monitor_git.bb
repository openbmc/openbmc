SUMMARY = "Phosphor DBus Monitor"
DESCRIPTION = "Phosphor DBus Monitor is a general purpose DBus application \
that watches DBus traffic for events and takes actions based on those events."
HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-monitor"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += " \
        ${PN}-config \
        phosphor-logging \
        ${PYTHON_PN}-sdbus++-native \
        sdeventplus \
        gtest \
        phosphor-snmp \
        ${PYTHON_PN}-native \
        ${PYTHON_PN}-pyyaml-native \
        ${PYTHON_PN}-setuptools-native \
        ${PYTHON_PN}-mako-native \
        "
SRCREV = "9c2f94e155b86ef234f2cc8aa39d4e18a15a0561"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-dbus-monitor;branch=master;protocol=https"

SYSTEMD_PACKAGES = "${PN} phosphor-msl-verify"
SYSTEMD_SERVICE:${PN} = "phosphor-dbus-monitor.service"
SYSTEMD_SERVICE:phosphor-msl-verify = "phosphor-msl-verify.service"
S = "${WORKDIR}/git"

inherit meson \
        pkgconfig \
        python3native \
        phosphor-dbus-monitor \
        obmc-phosphor-systemd

EXTRA_OEMESON = " \
        -DYAML_PATH=${STAGING_DIR_HOST}${config_dir} \
        "

FILES:phosphor-msl-verify = "${bindir}/phosphor-msl-verify"

PACKAGE_BEFORE_PN = "phosphor-msl-verify"
