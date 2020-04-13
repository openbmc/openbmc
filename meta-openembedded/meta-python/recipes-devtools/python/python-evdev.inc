SUMMARY = "Python evdev lib"
HOMEPAGE = "https://github.com/gvalkov/python-evdev"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18debddbb3f52c661a129724a883a8e2"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-evdev:"

SRC_URI = "${PYPI_SRC_URI}"

SRC_URI[md5sum] = "05f9e900d6e11e1674475d2dd2668f0d"
SRC_URI[sha256sum] = "b1c649b4fed7252711011da235782b2c260b32e004058d62473471e5cd30634d"

do_compile_prepend() {
    rm -rf ${S}/evdev/ecodes.c
}

DISTUTILS_BUILD_ARGS = "build_ecodes --evdev-headers ${STAGING_DIR_TARGET}/usr/include/linux/input.h:${STAGING_DIR_TARGET}/usr/include/linux/input-event-codes.h"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-stringold \
    "
