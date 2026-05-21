SUMMARY = "Pytest plugin to create CodSpeed benchmarks"
HOMEPAGE = "https://codspeed.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2856cbe664e8843cd4fd4c1d1d85c2c3"

DEPENDS = "python3-cffi-native"
SRC_URI[sha256sum] = "deb6ab9c9b07eba56fcb7b97206c7e48aaff697b6f73a013d8dbe4f62e76afd3"

inherit pypi python_hatchling

PYPI_PACKAGE = "pytest_codspeed"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} = "python3-cffi python3-filelock python3-pytest python3-rich python3-statistics"
