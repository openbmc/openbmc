SUMMARY = "Python utilities for working with Ethereum ABI definitions, especially encoding and decoding."
HOMEPAGE = "https://github.com/ethereum/eth-abi"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bf9691ead96f1163622689e47ce3f366"

SRC_URI[sha256sum] = "31578b179cf9430c21ac32a4e5f401c14b6e2cc1fd64ca3587cd354068217804"

PYPI_PACKAGE = "eth_abi"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-typing \
    python3-eth-utils \
    python3-parsimonious \
    python3-setuptools \
"
