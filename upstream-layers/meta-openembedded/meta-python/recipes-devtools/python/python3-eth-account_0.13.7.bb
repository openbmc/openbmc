SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a26e64020156e806cf0054a6d504b301"

SRC_URI[sha256sum] = "5853ecbcbb22e65411176f121f5f24b8afeeaf13492359d254b16d8b18c77a46"

PYPI_PACKAGE = "eth_account"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-bitarray \
    python3-eth-abi \
    python3-eth-keyfile \
    python3-eth-keys \
    python3-eth-rlp \
    python3-eth-utils \
    python3-hexbytes \
    python3-rlp \
    python3-ckzg \
"
