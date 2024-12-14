SUMMARY = "deflate64 compression/decompression library"
HOMEPAGE = "https://codeberg.org/miurahr/inflate64"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit python_setuptools_build_meta pypi

SRC_URI += "file://0001-Do-not-override-const-qualifier.patch"
SRC_URI[sha256sum] = "3278827b803cf006a1df251f3e13374c7d26db779e5a33329cc11789b804bc2d"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-core \
    python3-importlib-metadata \
"
