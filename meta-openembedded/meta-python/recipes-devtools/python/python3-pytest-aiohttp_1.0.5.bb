SUMMARY = "pytest plugin for aiohttp support"
HOMEPAGE = "https://github.com/aio-libs/pytest-aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c251ea89d32d196dddd14c669ed3d482"

DEPENDS = "python3-setuptools-scm-native"
SRC_URI[sha256sum] = "880262bc5951e934463b15e3af8bb298f11f7d4d3ebac970aab425aff10a780a"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "pytest-aiohttp"
