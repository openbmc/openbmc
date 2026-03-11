SUMMARY = "deflate64 compression/decompression library"
HOMEPAGE = "https://codeberg.org/miurahr/inflate64"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit python_setuptools_build_meta pypi

SRC_URI[sha256sum] = "a89edd416c36eda0c3a5d32f31ff1555db2c5a3884aa8df95e8679f8203e12ee"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-core \
    python3-importlib-metadata \
"
