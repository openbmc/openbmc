SUMMARY = "Universal encoding detector for Python 2 and 3"
HOMEPAGE = "https://pypi.org/project/chardet/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "1b3b6ff479a8c414bc3fa2c0852995695c4a026dcd6d0633b2dd092ca39c1cf7"

inherit pypi python_setuptools_build_meta

PACKAGES =+ "${PN}-cli"
FILES:${PN}-cli += " \
    ${PYTHON_SITEPACKAGES_DIR}/chardet/cli \
"

RDEPENDS:${PN}-cli = "${PN} "

RDEPENDS:${PN}:class-target += " \
    python3-logging \
"

BBCLASSEXTEND = "native nativesdk"
