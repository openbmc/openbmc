SUMMARY = "A network address manipulation library for Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6345d695ffe3776f68a56fe7962db44"

SRC_URI[md5sum] = "34cad578473b66ad77bc3b2a7613ed4a"
SRC_URI[sha256sum] = "d6cc57c7a07b1d9d2e917aa8b36ae8ce61c35ba3fcd1b83ca31c5a0ee2b5a243"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-xml \
"
