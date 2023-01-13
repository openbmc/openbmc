SUMMARY = "Universal encoding detector for Python 2 and 3"
HOMEPAGE = "https://pypi.org/project/chardet/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "0d62712b956bc154f85fb0a266e2a3c5913c2967e00348701b32411d6def31e5"

# setup.py of chardet needs this.
DEPENDS += "${PYTHON_PN}-pytest-runner-native"

inherit pypi python_setuptools_build_meta

PACKAGES =+ "${PN}-cli"
FILES:${PN}-cli += " \
    ${PYTHON_SITEPACKAGES_DIR}/chardet/cli \
"

RDEPENDS:${PN}-cli = "${PN} "

RDEPENDS:${PN}:class-target += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
