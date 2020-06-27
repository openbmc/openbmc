SUMMARY = "py.test plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

PYPI_PACKAGE = "pytest-timeout"

SRC_URI[md5sum] = "c458dd7d417a5f2dbae0f1f6073845f7"
SRC_URI[sha256sum] = "6d0fb4ce74cebb81be252e4e0d9c2a91f30270b33208cfa0f1da6eed9abf18ac"

inherit pypi setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-pytest"
