DESCRIPTION = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4ab25949a73fe7d4fdee93bcbdbf8ff"

SRC_URI[sha256sum] = "e0d0eb91441a3b53dab4d9b743eafc1ac44476296a2053b6ca3af0b139faf87b"
SRC_URI[md5sum] = "3f3f3e0750dab74cfa1dc8b0fd7a5f86"

inherit pypi

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-ctypes \
"    
