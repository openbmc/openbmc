SUMMARY = "Python evdev lib"
HOMEPAGE = "https://github.com/gvalkov/python-evdev"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ff275b442f45fc06287544cf713016f"

SRC_URI[sha256sum] = "299db8628cc73b237fc1cc57d3c2948faa0756e2a58b6194b5bf81dc2081f1e3"

inherit pypi setuptools3

do_compile:prepend() {
    rm -rf ${S}/evdev/ecodes.c
}

SETUPTOOLS_BUILD_ARGS = "build_ecodes --evdev-headers ${STAGING_DIR_TARGET}/usr/include/linux/input.h:${STAGING_DIR_TARGET}/usr/include/linux/input-event-codes.h"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-stringold \
    "
