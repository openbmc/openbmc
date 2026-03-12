SUMMARY = "pytest xdist plugin for distributed testing and loop-on-failing modes"
HOMEPAGE = "https://github.com/pytest-dev/pytest-xdist"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fbae836e16c18f4b220f7db6564e8f61"

SRC_URI[sha256sum] = "7e578125ec9bc6050861aa93f2d59f1d8d085595d6551c2c90b6f4fad8d3a9f1"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-execnet \
    python3-pytest \
"

PYPI_PACKAGE = "pytest_xdist"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
