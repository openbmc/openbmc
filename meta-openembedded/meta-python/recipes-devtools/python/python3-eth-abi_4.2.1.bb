SUMMARY = "Python utilities for working with Ethereum ABI definitions, especially encoding and decoding."
HOMEPAGE = "https://github.com/ethereum/eth-abi"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=731f4de9c79bfeba6d8d55f83d0d2423"

SRC_URI[sha256sum] = "60d88788d53725794cdb07c0f0bb0df2a31a6e1ad19644313fe6117ac24eeeb0"

PYPI_PACKAGE = "eth_abi"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-typing \
    python3-eth-utils \
    python3-parsimonious \
    python3-setuptools \
"
