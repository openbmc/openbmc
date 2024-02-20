SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16ffc86adf4293d4cfb204e77d62cfe6"

SRC_URI[sha256sum] = "2ffc7a0c7538053a06a7d11495c16c7ad9897dd42be0f64ca7551e9f6e0738c3"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-bitarray \
    ${PYTHON_PN}-eth-abi \
    ${PYTHON_PN}-eth-keyfile \
    ${PYTHON_PN}-eth-keys \
    ${PYTHON_PN}-eth-rlp \
    ${PYTHON_PN}-eth-utils \
    ${PYTHON_PN}-hexbytes \
    ${PYTHON_PN}-rlp \
"
