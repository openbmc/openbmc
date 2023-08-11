# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "execnet: rapid multi-Python deployment"
HOMEPAGE = "https://execnet.readthedocs.io/en/latest/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=772fcdaca14b378878d05c7d857e6c3e"

DEPENDS += "\
    python3-pip-native \
    python3-hatch-vcs-native \
"

SRC_URI += "file://run-ptest \
           "
SRC_URI[sha256sum] = "cc59bc4423742fd71ad227122eb0dd44db51efb3dc4095b45ac9a08c770096af"

inherit ptest pypi python_hatchling

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/testing/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += "\
    python3-pytest \
"

RDEPENDS:${PN} += "python3-core python3-crypt python3-ctypes python3-fcntl python3-io python3-shell python3-threading"

BBCLASSEXTEND = "native nativesdk"
