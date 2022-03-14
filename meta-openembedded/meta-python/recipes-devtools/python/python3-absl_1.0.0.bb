SUMMARY = "Abseil Python Common Libraries"
HOMEPAGE = "https://github.com/abseil/abseil-py"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "ac511215c01ee9ae47b19716599e8ccfa746f2e18de72bdf641b79b22afa27ea"

PYPI_PACKAGE = "absl-py"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
