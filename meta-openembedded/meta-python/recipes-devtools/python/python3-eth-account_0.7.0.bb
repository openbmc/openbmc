SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=287820ad3553117aa2f92bf84c219324"

SRC_URI[sha256sum] = "61360e9e514e09e49929ed365ca0e1656758ecbd11248c629ad85b4335c2661a"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-bitarray \
    python3-cytoolz \
    python3-eth-abi \
    python3-eth-keyfile \
    python3-eth-rlp \
    python3-hexbytes \
"
