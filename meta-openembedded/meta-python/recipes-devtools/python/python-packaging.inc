DESCRIPTION = "Core utilities for Python packages"
HOMEPAGE = "https://github.com/pypa/packaging"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faadaedca9251a90b205c9167578ce91"

SRC_URI[md5sum] = "867ce70984dc7b89bbbc3cac2a72b171"
SRC_URI[sha256sum] = "28b924174df7a2fa32c1953825ff29c61e2f5e082343165438812f00d3a7fc47"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS_${PN} += "${PYTHON_PN}-six ${PYTHON_PN}-pyparsing"
