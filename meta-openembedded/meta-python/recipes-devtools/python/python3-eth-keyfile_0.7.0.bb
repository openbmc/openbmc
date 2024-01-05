SUMMARY = "A library for handling the encrypted keyfiles used to store ethereum private keys."
HOMEPAGE = "https://github.com/ethereum/eth-keyfile"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6749008d847c14b9718949c2e24d5c0a"

SRC_URI[sha256sum] = "6bdb8110c3a50439deb68a04c93c9d5ddd5402353bfae1bf4cfca1d6dff14fcf"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-keys \
    python3-pycryptodome \
    python3-setuptools \
"
