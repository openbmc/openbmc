SUMMARY = "deflate64 compression/decompression library"
HOMEPAGE = "https://codeberg.org/miurahr/inflate64"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit setuptools3 pypi

SRC_URI[sha256sum] = "3278827b803cf006a1df251f3e13374c7d26db779e5a33329cc11789b804bc2d"

PYPI_PACKAGE = "inflate64"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-importlib-metadata \
"
