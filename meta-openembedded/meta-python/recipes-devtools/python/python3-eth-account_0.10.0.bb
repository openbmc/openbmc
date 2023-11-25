SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16ffc86adf4293d4cfb204e77d62cfe6"

SRC_URI[sha256sum] = "474a2fccf7286230cf66502565f03b536921d7e1fdfceba198e42160e5ac4bc1"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-bitarray \
    python3-cytoolz \
    python3-eth-abi \
    python3-eth-keyfile \
    python3-eth-rlp \
    python3-hexbytes \
"
