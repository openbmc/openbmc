SUMMARY = "Send responses to httpx."
HOMEPAGE = "https://github.com/Colin-b/pytest_httpx"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8dcb0d82b1d402b6be745fc78dde254b"
RECIPE_MAINTAINER = "Tom Geelen <t.f.g.geelen@gmail.com>"

SRC_URI[sha256sum] = "9edb66a5fd4388ce3c343189bc67e7e1cb50b07c2e3fc83b97d511975e8a831b"

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
