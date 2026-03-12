SUMMARY = "deflate64 compression/decompression library"
HOMEPAGE = "https://codeberg.org/miurahr/inflate64"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit python_setuptools_build_meta pypi

SRC_URI[sha256sum] = "b398c686960c029777afc0ed281a86f66adb956cfc3fbf6667cc6453f7b407ce"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-core \
    python3-importlib-metadata \
"
