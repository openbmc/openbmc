SUMMARY = "Common utility functions for codebases which interact with ethereum."
HOMEPAGE = "https://github.com/ethereum/eth-utils"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6749008d847c14b9718949c2e24d5c0a"

SRC_URI[sha256sum] = "84c6314b9cf1fcd526107464bbf487e3f87097a2e753360d5ed319f7d42e3f20"

PYPI_PACKAGE = "eth_utils"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-hash \
    python3-eth-typing \
    python3-setuptools \
    python3-toolz \
"
