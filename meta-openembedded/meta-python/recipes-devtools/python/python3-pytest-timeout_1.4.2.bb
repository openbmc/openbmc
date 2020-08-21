SUMMARY = "py.test plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

PYPI_PACKAGE = "pytest-timeout"

SRC_URI[md5sum] = "552cc293447b00f7a294ce7a1fb3839f"
SRC_URI[sha256sum] = "20b3113cf6e4e80ce2d403b6fb56e9e1b871b510259206d40ff8d609f48bda76"

inherit pypi setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-pytest"
