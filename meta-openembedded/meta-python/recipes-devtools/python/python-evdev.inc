SUMMARY = "Python evdev lib"
HOMEPAGE = "https://github.com/gvalkov/python-evdev"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18debddbb3f52c661a129724a883a8e2"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-evdev:"

SRC_URI += " file://0001-Fix-build-on-32bit-arches-with-64bit-time_t.patch"

SRC_URI[md5sum] = "53e440943dfa2514f95b3c448d6a36cb"
SRC_URI[sha256sum] = "b03f5e1be5b4a5327494a981b831d251a142b09e8778eda1a8b53eba91100166"

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
