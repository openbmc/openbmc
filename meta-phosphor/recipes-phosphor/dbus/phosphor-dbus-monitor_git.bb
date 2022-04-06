SUMMARY = "Phosphor DBus Monitor"
DESCRIPTION = "Phosphor DBus Monitor is a general purpose DBus application \
that watches DBus traffic for events and takes actions based on those events."
PR = "r1"
PV = "1.0+git${SRCPV}"
HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-monitor"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI = "git://github.com/openbmc/phosphor-dbus-monitor;branch=master;protocol=https"
SRCREV = "afa54c68455d70960aafb02665da14e2f20332ee"

inherit autotools \
        pkgconfig \
        python3native \
        phosphor-dbus-monitor \
        obmc-phosphor-systemd

PACKAGE_BEFORE_PN = "phosphor-msl-verify"
SYSTEMD_PACKAGES = "${PN} phosphor-msl-verify"
SYSTEMD_SERVICE:phosphor-msl-verify = "phosphor-msl-verify.service"

DEPENDS += " \
        ${PN}-config \
        phosphor-logging \
        autoconf-archive-native \
        ${PYTHON_PN}-sdbus++-native \
        sdeventplus \
        gtest \
        phosphor-snmp \
        ${PYTHON_PN}-native \
        ${PYTHON_PN}-pyyaml-native \
        ${PYTHON_PN}-setuptools-native \
        ${PYTHON_PN}-mako-native \
        "

FILES:phosphor-msl-verify = "${bindir}/phosphor-msl-verify"

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
        YAML_PATH=${STAGING_DIR_HOST}${config_dir} \
        "
