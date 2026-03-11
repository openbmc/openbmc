SUMMARY = "pytest xdist plugin for distributed testing and loop-on-failing modes"
HOMEPAGE = "https://github.com/pytest-dev/pytest-xdist"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fbae836e16c18f4b220f7db6564e8f61"

SRC_URI[sha256sum] = "f9248c99a7c15b7d2f90715df93610353a485827bc06eefb6566d23f6400f126"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-execnet \
    python3-pytest \
"

PYPI_PACKAGE = "pytest_xdist"
