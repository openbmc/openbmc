DESCRIPTION = "The leading native Python SSHv2 protocol library."
HOMEPAGE = "https://github.com/paramiko/paramiko/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd0120fc2e9f841c73ac707a30389af5"

SRC_URI[sha256sum] = "ddb1977853aef82804b35d72a0e597b244fa326c404c350bd00c5b01dbfee71a"

PYPI_PACKAGE = "paramiko"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-bcrypt \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-pynacl \
    ${PYTHON_PN}-unixadmin \
"
