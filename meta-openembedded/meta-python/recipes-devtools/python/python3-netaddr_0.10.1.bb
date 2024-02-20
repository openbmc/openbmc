SUMMARY = "A network address manipulation library for Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6345d695ffe3776f68a56fe7962db44"

SRC_URI[sha256sum] = "f4da4222ca8c3f43c8e18a8263e5426c750a3a837fdfeccf74c68d0408eaa3bf"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-io \
    python3-pprint \
    python3-xml \
"
