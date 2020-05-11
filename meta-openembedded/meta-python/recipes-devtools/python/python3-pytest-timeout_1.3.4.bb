SUMMARY = "py.test plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

PYPI_PACKAGE = "pytest-timeout"

SRC_URI[md5sum] = "1594762ae77ed7c6c2727aa8b4aa8bfb"
SRC_URI[sha256sum] = "80faa19cd245a42b87a51699d640c00d937c02b749052bfca6bae8bdbe12c48e"

inherit pypi setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-pytest"
