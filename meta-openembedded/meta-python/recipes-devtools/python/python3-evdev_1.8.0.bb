SUMMARY = "Python evdev lib"
HOMEPAGE = "https://github.com/gvalkov/python-evdev"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7bd1cc4c71b706c7e2d4053aef50f2a"

SRC_URI[sha256sum] = "45598eee1ae3876a3122ca1dc0ec8049c01931672d12478b5c610afc24e47d75"

inherit pypi python_setuptools_build_meta

do_compile:prepend() {
    rm -rf ${S}/evdev/ecodes.c
}

SETUPTOOLS_BUILD_ARGS = "build_ecodes --evdev-headers ${STAGING_DIR_TARGET}/usr/include/linux/input.h:${STAGING_DIR_TARGET}/usr/include/linux/input-event-codes.h"

RDEPENDS:${PN} += "\
    python3-ctypes \
    python3-fcntl \
    python3-io \
    python3-shell \
    python3-stringold \
    "
