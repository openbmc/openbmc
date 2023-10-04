SUMMARY = "Abseil Python Common Libraries"
HOMEPAGE = "https://github.com/abseil/abseil-py"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "d9690211c5fcfefcdd1a45470ac2b5c5acd45241c3af71eed96bc5441746c0d5"

PYPI_PACKAGE = "absl-py"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
