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

SRC_URI[sha256sum] = "63d83bfdd9a23e35b9c6a3261412324f964c2ec8dcd8d3c6916ee9373e0befcd"

inherit ptest-python-pytest pypi python_hatchling

PTEST_PYTEST_DIR = "testing"

RDEPENDS:${PN} += "python3-core python3-crypt python3-ctypes python3-fcntl python3-io python3-shell python3-threading"

BBCLASSEXTEND = "native nativesdk"
