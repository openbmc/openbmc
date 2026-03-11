SUMMARY = "Abseil Python Common Libraries"
HOMEPAGE = "https://github.com/abseil/abseil-py"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "7820790efbb316739cde8b4e19357243fc3608a152024288513dd968d7d959ff"

PYPI_PACKAGE = "absl-py"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
