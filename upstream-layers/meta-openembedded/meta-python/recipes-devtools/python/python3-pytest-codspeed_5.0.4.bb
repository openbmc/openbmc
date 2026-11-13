SUMMARY = "Pytest plugin to create CodSpeed benchmarks"
HOMEPAGE = "https://codspeed.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2856cbe664e8843cd4fd4c1d1d85c2c3"

DEPENDS = "python3-cffi-native"
SRC_URI[sha256sum] = "5098065fb9f91d80a7a81752d1970153e44036fe36ce0b6a7afa6bf7881b9d3e"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "python3-cffi python3-filelock python3-pytest python3-rich python3-statistics"
