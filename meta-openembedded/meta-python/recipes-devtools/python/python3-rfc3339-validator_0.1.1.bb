SUMMARY = "A pure python RFC3339 validator"
HOMEPAGE = "https://github.com/naimetti/rfc3339-validator"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a21b13b5a996f08f7e0b088aa38ce9c6"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-rfc3339-validator:"

SRC_URI[md5sum] = "9d8899041d83f98180bddd8b62ee7e99"
SRC_URI[sha256sum] = "20a600d01fbb1f793cbb6f4ec4ebb2104f4c9e80d74d5f78350b63ecc6cccd08"

PYPI_PACKAGE = "rfc3339_validator"

inherit pypi setuptools3

SRC_URI_append = " \
    file://0001-setup.py-move-pytest-runner-to-test_requirements.patch \
"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
