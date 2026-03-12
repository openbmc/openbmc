DESCRIPTION = "python-libevdev is a Python wrapper around the libevdev C library."
HOMEPAGE = "https://gitlab.freedesktop.org/libevdev/python-libevdev"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d94c10c546b419eddc6296157ec40747"

SRC_URI[sha256sum] = "dc3369cd1401767b9ecb1117cd6b73faba9038e3bd9e1695a710a9e9d9415e8d"

inherit pypi python_hatchling ptest

PYPI_PACKAGE = "libevdev"

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN} += " \
    libevdev \
    python3-ctypes \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
}

