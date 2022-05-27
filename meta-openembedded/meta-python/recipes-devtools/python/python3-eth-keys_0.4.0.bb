SUMMARY = "A common API for Ethereum key operations with pluggable backends."
HOMEPAGE = "https://github.com/ethereum/eth-keys"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2beaef1b1764f4d6b46084c885b4bcad"

SRC_URI[sha256sum] = "7d18887483bc9b8a3fdd8e32ddcb30044b9f08fcb24a380d93b6eee3a5bb3216"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-eth-utils"
