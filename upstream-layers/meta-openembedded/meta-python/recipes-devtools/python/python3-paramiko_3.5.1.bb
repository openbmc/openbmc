DESCRIPTION = "The leading native Python SSHv2 protocol library."
HOMEPAGE = "https://github.com/paramiko/paramiko/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd0120fc2e9f841c73ac707a30389af5"

SRC_URI[sha256sum] = "b2c665bc45b2b215bd7d7f039901b14b067da00f3a11e6640995fd58f2664822"

PYPI_PACKAGE = "paramiko"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    python3-bcrypt \
    python3-cryptography \
    python3-logging \
    python3-pynacl \
    python3-unixadmin \
"
