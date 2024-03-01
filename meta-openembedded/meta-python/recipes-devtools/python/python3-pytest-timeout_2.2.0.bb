SUMMARY = "py.test plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

PYPI_PACKAGE = "pytest-timeout"

SRC_URI[sha256sum] = "3b0b95dabf3cb50bac9ef5ca912fa0cfc286526af17afc806824df20c2f72c90"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-pytest"
