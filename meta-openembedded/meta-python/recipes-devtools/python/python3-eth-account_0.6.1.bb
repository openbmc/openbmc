SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=287820ad3553117aa2f92bf84c219324"

SRC_URI[sha256sum] = "54b0b7d661e73f4cd12d508c9baa5c9a6e8c194aa7bafc39277cd673683ae50e"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-bitarray \
    python3-cytoolz \
    python3-eth-abi \
    python3-eth-keyfile \
    python3-eth-rlp \
    python3-hexbytes \
"
