SUMMARY = "py.test plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

PYPI_PACKAGE = "pytest-timeout"

SRC_URI[sha256sum] = "a5ec4eceddb8ea726911848593d668594107e797621e97f93a1d1dbc6fbb9080"

inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-pytest"
