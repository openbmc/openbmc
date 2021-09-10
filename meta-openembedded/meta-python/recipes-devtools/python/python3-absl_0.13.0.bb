SUMMARY = "Abseil Python Common Libraries"
HOMEPAGE = "https://github.com/abseil/abseil-py"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "afb3ffb4b59d53575e8eb073c3a52803"
SRC_URI[sha256sum] = "6953272383486044699fd0e9f00aad167a27e08ce19aae66c6c4b10e7e767793"

PYPI_PACKAGE = "absl-py"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
