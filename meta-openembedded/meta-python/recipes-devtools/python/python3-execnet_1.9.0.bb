# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "execnet: rapid multi-Python deployment"
HOMEPAGE = "https://execnet.readthedocs.io/en/latest/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=772fcdaca14b378878d05c7d857e6c3e"

DEPENDS += "\
    python3-pip-native \
    python3-setuptools-scm-native \
"

SRC_URI += "file://run-ptest \
           "
SRC_URI[sha256sum] = "8f694f3ba9cc92cab508b152dcfe322153975c29bda272e2fd7f3f00f36e47c5"

inherit ptest pypi setuptools3

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/testing/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += "\
    python3-pytest \
"

RDEPENDS:${PN} += "python3-core python3-crypt python3-ctypes python3-fcntl python3-io python3-shell python3-threading"

BBCLASSEXTEND = "native nativesdk"
