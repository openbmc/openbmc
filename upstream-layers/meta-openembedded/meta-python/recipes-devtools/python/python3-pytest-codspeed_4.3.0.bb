SUMMARY = "Pytest plugin to create CodSpeed benchmarks"
HOMEPAGE = "https://codspeed.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2856cbe664e8843cd4fd4c1d1d85c2c3"

DEPENDS = "python3-cffi-native"
SRC_URI[sha256sum] = "5230d9d65f39063a313ed1820df775166227ec5c20a1122968f85653d5efee48"

inherit pypi python_hatchling

PYPI_PACKAGE = "pytest_codspeed"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} = "python3-cffi python3-filelock python3-pytest python3-rich python3-statistics"
