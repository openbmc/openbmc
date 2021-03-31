SUMMARY = "A pure python RFC3339 validator"
HOMEPAGE = "https://github.com/naimetti/rfc3339-validator"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a21b13b5a996f08f7e0b088aa38ce9c6"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-rfc3339-validator:"

SRC_URI[sha256sum] = "7a578aa0740e9ee2b48356fe1f347139190c4c72e27f303b3617054efd15df32"

PYPI_PACKAGE = "rfc3339_validator"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
