SUMMARY = "Pytest plugin providing a fixture interface for spulec/freezegun"
HOMEPAGE = "https://github.com/pytest-dev/pytest-freezer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1661a0f2b9b83ed73b8e05b5683b10d0"

SRC_URI[sha256sum] = "21bf16bc9cc46bf98f94382c4b5c3c389be7056ff0be33029111ae11b3f1c82a"

inherit pypi python_flit_core

RDEPENDS:${PN} = "\
    python3-freezegun (>=1.0) \
    python3-pytest (>=3.6) \
"

PYPI_PACKAGE = "pytest_freezer"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
