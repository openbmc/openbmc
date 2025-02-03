SUMMARY = "deflate64 compression/decompression library"
HOMEPAGE = "https://codeberg.org/miurahr/inflate64"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit python_setuptools_build_meta pypi

SRC_URI[sha256sum] = "3b1c83c22651b5942b35829df526e89602e494192bf021e0d7d0b600e76c429d"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-core \
    python3-importlib-metadata \
"
