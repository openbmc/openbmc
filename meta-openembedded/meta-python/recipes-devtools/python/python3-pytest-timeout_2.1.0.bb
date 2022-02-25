SUMMARY = "py.test plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

PYPI_PACKAGE = "pytest-timeout"

SRC_URI[sha256sum] = "c07ca07404c612f8abbe22294b23c368e2e5104b521c1790195561f37e1ac3d9"

inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-pytest"
