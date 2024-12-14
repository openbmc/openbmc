DESCRIPTION="Creates diffs of XML files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0d0e9e3949e163c3edd1e097b8b0ed62"

SRC_URI[sha256sum] = "19b030b3fa37d1f0b5c5ad9ada9059884c3bf2c751c5dd8f1eb4ed49cfe3fc60"

PYPI_PACKAGE = "xmldiff"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
"
