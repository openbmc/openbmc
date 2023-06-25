SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16ffc86adf4293d4cfb204e77d62cfe6"

SRC_URI[sha256sum] = "5f66ecb7bc52569924dfaf4a9add501b1c2a4901eec74e3c0598cd26d0971777"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-bitarray \
    python3-cytoolz \
    python3-eth-abi \
    python3-eth-keyfile \
    python3-eth-rlp \
    python3-hexbytes \
"
