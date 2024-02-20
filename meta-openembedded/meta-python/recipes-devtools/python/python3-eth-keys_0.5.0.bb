SUMMARY = "A common API for Ethereum key operations with pluggable backends."
HOMEPAGE = "https://github.com/ethereum/eth-keys"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6749008d847c14b9718949c2e24d5c0a"

SRC_URI[sha256sum] = "a0abccb83f3d84322591a2c047a1e3aa52ea86b185fa3e82ce311d120ca2791e"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-eth-utils"
