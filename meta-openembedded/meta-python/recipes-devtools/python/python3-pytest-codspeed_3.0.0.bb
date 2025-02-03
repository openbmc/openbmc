SUMMARY = "Pytest plugin to create CodSpeed benchmarks"
HOMEPAGE = "https://codspeed.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2856cbe664e8843cd4fd4c1d1d85c2c3"

DEPENDS = "python3-hatchling-native"
SRC_URI[sha256sum] = "c5b80100ea32dd44079bb2db298288763eb8fe859eafa1650a8711bd2c32fd06"

inherit pypi python_hatchling

PYPI_PACKAGE = "pytest_codspeed"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} = "python3-cffi python3-filelock python3-pytest"
