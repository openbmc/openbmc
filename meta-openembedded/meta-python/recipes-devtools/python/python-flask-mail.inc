SUMMARY = "Flask extension for sending email"
DESCRIPTION = "A Flask extension for sending email"
HOMEPAGE = " https://github.com/rduplain/flask-email"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5b16dfa6d3f275ace5985bb92949f770"

SRC_URI[md5sum] = "04b35a42a44ec7aa724ec8ce55e2e08e"
SRC_URI[sha256sum] = "22e5eb9a940bf407bcf30410ecc3708f3c56cc44b29c34e1726fe85006935f41"

PYPI_PACKAGE = "Flask-Mail"

RDEPENDS_${PN} = "${PYTHON_PN}-flask"
