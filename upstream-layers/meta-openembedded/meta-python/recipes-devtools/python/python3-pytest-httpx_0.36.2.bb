SUMMARY = "Send responses to httpx."
HOMEPAGE = "https://github.com/Colin-b/pytest_httpx"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b6746540997ba13df3f30783db069bc"
RECIPE_MAINTAINER = "Tom Geelen <t.f.g.geelen@gmail.com>"

SRC_URI[sha256sum] = "05a56527484f7f4e8c856419ea379b8dc359c36801c4992fdb330f294c690356"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-httpx (>=0.28) \
    python3-pytest (>=8) \
"

RDEPENDS:${PN}-ptest += "\
    python3-pytest-cov \
    python3-pytest-asyncio \
"

PYPI_PACKAGE = "pytest_httpx"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
