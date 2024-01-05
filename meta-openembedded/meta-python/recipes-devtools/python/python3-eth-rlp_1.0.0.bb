SUMMARY = "RLP definitions for common Ethereum objects in Python"
HOMEPAGE = "https://github.com/ethereum/eth-rlp"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d7bdfe69b1ffbde073ca6e96f5c53f7"

SRC_URI[sha256sum] = "a988d713a36452e7c6da71186798343f687eaf3aeb7f99266750dd9e1f754c7b"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-eth-utils \
    python3-hexbytes \
    python3-rlp \
"
