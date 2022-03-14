DESCRIPTION = "The leading native Python SSHv2 protocol library."
HOMEPAGE = "https://github.com/paramiko/paramiko/"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd0120fc2e9f841c73ac707a30389af5"

SRC_URI[sha256sum] = "944a9e5dbdd413ab6c7951ea46b0ab40713235a9c4c5ca81cfe45c6f14fa677b"

PYPI_PACKAGE = "paramiko"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-bcrypt \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-pynacl \
    ${PYTHON_PN}-unixadmin \
"
