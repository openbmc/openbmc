SUMMARY = "py.test plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

PYPI_PACKAGE = "pytest-timeout"

SRC_URI[sha256sum] = "e6f98b54dafde8d70e4088467ff621260b641eb64895c4195b6e5c8f45638112"

inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-pytest"
