SUMMARY = "Universal encoding detector for Python 2 and 3"
HOMEPAGE = "https://pypi.org/project/chardet/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "0368df2bfd78b5fc20572bb4e9bb7fb53e2c094f60ae9993339e8671d0afb8aa"

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
