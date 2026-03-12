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

SRC_URI[sha256sum] = "147de8cb164f3fc9d7196967f109ab3c0b93ea3463ab50631e56438eab7b5adc"

PYPI_PACKAGE = "pytest_aiohttp"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-aiohttp"
