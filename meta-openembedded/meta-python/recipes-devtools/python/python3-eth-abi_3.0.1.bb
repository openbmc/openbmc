SUMMARY = "Python utilities for working with Ethereum ABI definitions, especially encoding and decoding."
HOMEPAGE = "https://github.com/ethereum/eth-abi"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bf9691ead96f1163622689e47ce3f366"

SRC_URI[sha256sum] = "c3872e3ac1e9ef3f8c6599aaca4ee536d536eefca63a6892ab937f0560edb656"

PYPI_PACKAGE = "eth_abi"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-typing \
    python3-eth-utils \
    python3-parsimonious \
    python3-setuptools \
"
