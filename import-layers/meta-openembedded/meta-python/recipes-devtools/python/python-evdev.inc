SUMMARY = "Python evdev lib"
HOMEPAGE = "https://github.com/gvalkov/python-evdev"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18debddbb3f52c661a129724a883a8e2"

SRC_URI[md5sum] = "c7e855ae9f97f869a59e75b29f05ce74"
SRC_URI[sha256sum] = "57edafc469a414f58b51af1bfb9ee2babb9f626dd2df530d71c1176871850aa1"

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
