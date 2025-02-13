SUMMARY = "Common utility functions for codebases which interact with ethereum."
HOMEPAGE = "https://github.com/ethereum/eth-utils"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d3f53e7cc5bf80b16eff1f4a38c73182"

SRC_URI[sha256sum] = "17e474eb654df6e18f20797b22c6caabb77415a996b3ba0f3cc8df3437463134"

PYPI_PACKAGE = "eth_utils"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-hash \
    python3-eth-typing \
    python3-setuptools \
    python3-toolz \
"
