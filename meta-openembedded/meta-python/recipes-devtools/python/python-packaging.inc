DESCRIPTION = "Core utilities for Python packages"
HOMEPAGE = "https://github.com/pypa/packaging"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faadaedca9251a90b205c9167578ce91"

SRC_URI[md5sum] = "19e0d1f82a9007b448650ccfeffd0a26"
SRC_URI[sha256sum] = "3c292b474fda1671ec57d46d739d072bfd495a4f51ad01a055121d81e952b7a3"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS_${PN} += "${PYTHON_PN}-six ${PYTHON_PN}-pyparsing"
