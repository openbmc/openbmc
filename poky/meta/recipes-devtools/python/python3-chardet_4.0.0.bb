SUMMARY = "Universal encoding detector for Python 2 and 3"
HOMEPAGE = "https://pypi.org/project/chardet/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI[sha256sum] = "0d6f53a15db4120f2b08c94f11e7d93d2c911ee118b6b30a04ec3ee8310179fa"

# setup.py of chardet needs this.
DEPENDS += "${PYTHON_PN}-pytest-runner-native"

inherit pypi setuptools3

PACKAGES =+ "${PN}-cli"
FILES:${PN}-cli += " \
    ${PYTHON_SITEPACKAGES_DIR}/chardet/cli \
"

RDEPENDS:${PN}-cli = "${PN} "

RDEPENDS:${PN}:class-target += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
