DESCRIPTION = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4ab25949a73fe7d4fdee93bcbdbf8ff"

SRC_URI[sha256sum] = "26b54ddbbb9ee1d34d5d3668dd37d6cf74990ab23c828c2888dccdceee395594"
SRC_URI[md5sum] = "7be95e1b35e9385d71a0017a48217efc"

inherit pypi setuptools3

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-ctypes \
"
RDEPENDS_${PN}_class-nativesdk += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
