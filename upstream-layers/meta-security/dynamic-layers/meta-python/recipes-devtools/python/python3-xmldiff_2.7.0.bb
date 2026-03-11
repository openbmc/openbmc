DESCRIPTION = "Creates diffs of XML files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0d0e9e3949e163c3edd1e097b8b0ed62"

SRC_URI[sha256sum] = "c0910b1f800366dd7ec62923e5d06e8b06a1bd9120569a1c27f4f2446b9c68a2"

PYPI_PACKAGE = "xmldiff"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
    python3-json \
    python3-email \
    python3-lxml \
    python3-difflib \
"
