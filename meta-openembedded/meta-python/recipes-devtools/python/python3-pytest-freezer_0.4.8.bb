SUMMARY = "Pytest plugin providing a fixture interface for spulec/freezegun"
HOMEPAGE = "https://github.com/pytest-dev/pytest-freezer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1661a0f2b9b83ed73b8e05b5683b10d0"

SRC_URI[sha256sum] = "8ee2f724b3ff3540523fa355958a22e6f4c1c819928b78a7a183ae4248ce6ee6"

inherit pypi python_flit_core

RDEPENDS:${PN} = "\
    python3-freezegun (>=1.0) \
    python3-pytest (>=3.6) \
"

PYPI_PACKAGE = "pytest_freezer"
