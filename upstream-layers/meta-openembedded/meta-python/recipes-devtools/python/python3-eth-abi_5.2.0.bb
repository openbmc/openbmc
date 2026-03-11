SUMMARY = "Python utilities for working with Ethereum ABI definitions, especially encoding and decoding."
HOMEPAGE = "https://github.com/ethereum/eth-abi"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b6a85414b7d058ab1c58e11611c21b2"

SRC_URI[sha256sum] = "178703fa98c07d8eecd5ae569e7e8d159e493ebb6eeb534a8fe973fbc4e40ef0"

PYPI_PACKAGE = "eth_abi"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-typing \
    python3-eth-utils \
    python3-parsimonious \
    python3-setuptools \
"
