SUMMARY = "Phosphor python library"
DESCRIPTION = "Phosphor python library."
HOMEPAGE = "http://github.com/openbmc/pyphosphor"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit allarch
inherit setuptools
inherit python-dir

PACKAGE_BEFORE_PN = " \
        ${PN}-ns \
        ${PN}-utils-ns \
        ${PN}-dbus-ns \
        ${PN}-utils \
        ${PN}-dbus \
        "

RDEPENDS_${PN}-utils-ns += "${PN}-ns"
RDEPENDS_${PN}-dbus-ns += "${PN}-ns"
RDEPENDS_${PN}-utils += "${PN}-utils-ns"
RDEPENDS_${PN}-dbus += " \
        ${PN}-dbus-ns \
        python-dbus \
        python-xml \
        python-json \
        python-pickle \
        "
RDEPENDS_${PN} += " \
        ${PN}-ns \
        ${PN}-dbus \
        python-subprocess \
        python-dbus \
        "

FILES_${PN}-ns = "${PYTHON_SITEPACKAGES_DIR}/obmc/__init__.py*"
FILES_${PN}-utils-ns = "${PYTHON_SITEPACKAGES_DIR}/obmc/utils/__init__.py*"
FILES_${PN}-dbus-ns = "${PYTHON_SITEPACKAGES_DIR}/obmc/dbuslib/__init__.py*"

FILES_${PN}-utils = "${PYTHON_SITEPACKAGES_DIR}/obmc/utils"
FILES_${PN}-dbus = "${PYTHON_SITEPACKAGES_DIR}/obmc/dbuslib"

SRC_URI += "git://github.com/openbmc/pyphosphor"

SRCREV = "ecf8bd8f38d93fda3d08df771709bd5289a480e9"

S = "${WORKDIR}/git"
