SUMMARY = "pytest plugin for aiohttp support"
HOMEPAGE = "https://github.com/aio-libs/pytest-aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c251ea89d32d196dddd14c669ed3d482"

DEPENDS = "\
    python3-setuptools-scm-native \
    python3-pytest-native \
    python3-pytest-asyncio-native \
    python3-aiohttp-native \
"

SRC_URI[sha256sum] = "3aa9c9fe26e543eaccc7eb0add381c685ba3ed3e2fed0af74540f63bcd31458d"

PYPI_PACKAGE = "pytest_aiohttp"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-aiohttp"
