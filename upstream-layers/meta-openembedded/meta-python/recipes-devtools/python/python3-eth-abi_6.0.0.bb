SUMMARY = "Python utilities for working with Ethereum ABI definitions, especially encoding and decoding."
HOMEPAGE = "https://github.com/ethereum/eth-abi"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b6a85414b7d058ab1c58e11611c21b2"

SRC_URI[sha256sum] = "e83a0ed91f2dadeeb50236d673736fe2edc6fcc0a1c1e13d461192d4b23d5bcc"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-wheel-native \
    python3-setuptools-scm-native \
"

RDEPENDS:${PN} += "\
    python3-eth-typing \
    python3-eth-utils \
    python3-parsimonious \
    python3-setuptools \
"
