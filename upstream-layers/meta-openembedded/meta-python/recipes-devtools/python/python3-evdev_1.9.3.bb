SUMMARY = "Python evdev lib"
HOMEPAGE = "https://github.com/gvalkov/python-evdev"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7bd1cc4c71b706c7e2d4053aef50f2a"

SRC_URI[sha256sum] = "2c140e01ac8437758fa23fe5c871397412461f42d421aa20241dc8fe8cfccbc9"

inherit pypi python_setuptools_build_meta ptest-python-pytest

do_compile:prepend() {
    rm -rf ${S}/evdev/ecodes.c
}

PEP517_BUILD_OPTS = "--config-setting=--build-option='build_ecodes \
    --evdev-headers ${STAGING_DIR_TARGET}/usr/include/linux/input.h:${STAGING_DIR_TARGET}/usr/include/linux/input-event-codes.h:${STAGING_DIR_TARGET}/usr/include/linux/uinput.h \
    --reproducible'"

RDEPENDS:${PN} += "\
    python3-ctypes \
    python3-fcntl \
    python3-io \
    python3-shell \
    python3-stringold \
    "
