SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=287820ad3553117aa2f92bf84c219324"

SRC_URI[sha256sum] = "ccb2d90a16c81c8ea4ca4dc76a70b50f1d63cea6aff3c5a5eddedf9e45143eca"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-bitarray \
    python3-cytoolz \
    python3-eth-abi \
    python3-eth-keyfile \
    python3-eth-rlp \
    python3-hexbytes \
"
