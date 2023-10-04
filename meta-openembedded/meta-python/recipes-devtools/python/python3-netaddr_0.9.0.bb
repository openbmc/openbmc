SUMMARY = "A network address manipulation library for Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6345d695ffe3776f68a56fe7962db44"

SRC_URI[sha256sum] = "7b46fa9b1a2d71fd5de9e4a3784ef339700a53a08c8040f08baf5f1194da0128"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-io \
    python3-pprint \
    python3-xml \
"
