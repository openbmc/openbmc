SUMMARY = "py.test plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

SRC_URI[sha256sum] = "7e68e90b01f9eff71332b25001f85c75495fc4e3a836701876183c4bcfd0540a"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "python3-pytest"

PYPI_PACKAGE = "pytest_timeout"
