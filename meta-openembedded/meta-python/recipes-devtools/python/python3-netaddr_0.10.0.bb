SUMMARY = "A network address manipulation library for Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6345d695ffe3776f68a56fe7962db44"

SRC_URI[sha256sum] = "4c30c54adf4ea4318b3c055ea3d8c7f6554a50aa2cd8aea4605a23caa0b0229e"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-io \
    python3-pprint \
    python3-xml \
"
