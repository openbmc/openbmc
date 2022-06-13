SUMMARY = "RLP definitions for common Ethereum objects in Python"
HOMEPAGE = "https://github.com/ethereum/eth-rlp"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=287820ad3553117aa2f92bf84c219324"

SRC_URI[sha256sum] = "f3263b548df718855d9a8dbd754473f383c0efc82914b0b849572ce3e06e71a6"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-eth-utils \
    python3-hexbytes \
    python3-rlp \
"
