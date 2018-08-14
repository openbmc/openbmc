DESCRIPTION = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4ab25949a73fe7d4fdee93bcbdbf8ff"

SRC_URI[md5sum] = "d7a89063b2e0fd36bd82389c4d82821d"
SRC_URI[sha256sum] = "1c7960ccfd6a005cd9f7ba884e6316b5e430a3f1a6c37c5f87d8b43f83b54ec9"

inherit pypi

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-ctypes \
"    
