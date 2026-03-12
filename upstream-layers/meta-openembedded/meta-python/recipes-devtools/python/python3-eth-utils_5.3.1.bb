SUMMARY = "Common utility functions for codebases which interact with ethereum."
HOMEPAGE = "https://github.com/ethereum/eth-utils"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d3f53e7cc5bf80b16eff1f4a38c73182"

SRC_URI[sha256sum] = "c94e2d2abd024a9a42023b4ddc1c645814ff3d6a737b33d5cfd890ebf159c2d1"

PYPI_PACKAGE = "eth_utils"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-hash \
    python3-eth-typing \
    python3-setuptools \
    python3-toolz \
    python3-pydantic \
"
