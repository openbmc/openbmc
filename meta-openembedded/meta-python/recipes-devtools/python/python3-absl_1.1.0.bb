SUMMARY = "Abseil Python Common Libraries"
HOMEPAGE = "https://github.com/abseil/abseil-py"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "3aa39f898329c2156ff525dfa69ce709e42d77aab18bf4917719d6f260aa6a08"

PYPI_PACKAGE = "absl-py"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
