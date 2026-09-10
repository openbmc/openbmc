SUMMARY = "pytest plugin to abort hanging tests"
DESCRIPTION = "pytest plugin to abort hanging tests"
HOMEPAGE = "https://github.com/pytest-dev/pytest-timeout/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8048cd156eda3df2e7f111b0ae9ceff"

SRC_URI[sha256sum] = "68c9d82dfe95fa65712e107010c9344e7d8b4afe8c975a7be0d273d939583a93"

PYPI_PACKAGE = "pytest_timeout"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-core \
    python3-pytest \
    python3-pexpect \
"
