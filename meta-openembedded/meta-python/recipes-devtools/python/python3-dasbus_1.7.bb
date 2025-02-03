SUMMARY = "Dasbus is a DBus library written in Python 3, based on GLib and inspired by pydbus."
HOMEPAGE = "https://dasbus.readthedocs.io"
SECTION = "devel"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI = "git://github.com/dasbus-project/dasbus.git;protocol=https;branch=master \
"
SRCREV = "413cf9a0b7c231468f1d28d9a29dd8dd8bda15c5"

S = "${WORKDIR}/git"

inherit setuptools3 ptest-python-pytest

do_install_ptest:append () {
    # Remove the problematic cases
    # https://github.com/dasbus-project/dasbus/issues/128
    rm -rf ${D}${PTEST_PATH}/tests/lib_dbus.py
    rm -rf ${D}${PTEST_PATH}/tests/test_dbus.py
    rm -rf ${D}${PTEST_PATH}/tests/test_unix.py
}

RDEPENDS:${PN} = "python3-pygobject"
