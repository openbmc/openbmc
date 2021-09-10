DESCRIPTION = "Core utilities for Python packages"
HOMEPAGE = "https://github.com/pypa/packaging"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faadaedca9251a90b205c9167578ce91"

SRC_URI[sha256sum] = "7dc96269f53a4ccec5c0670940a4281106dd0bb343f47b7471f779df49c2fbe7"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS:${PN} += "${PYTHON_PN}-six ${PYTHON_PN}-pyparsing"
