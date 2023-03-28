SUMMARY = "A library for handling the encrypted keyfiles used to store ethereum private keys."
HOMEPAGE = "https://github.com/ethereum/eth-keyfile"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2beaef1b1764f4d6b46084c885b4bcad"

SRC_URI[sha256sum] = "471be6e5386fce7b22556b3d4bde5558dbce46d2674f00848027cb0a20abdc8c"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-keys \
    python3-pycryptodome \
    python3-setuptools \
"
