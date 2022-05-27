SUMMARY = "A library for handling the encrypted keyfiles used to store ethereum private keys."
HOMEPAGE = "https://github.com/ethereum/eth-keyfile"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2beaef1b1764f4d6b46084c885b4bcad"

SRC_URI[sha256sum] = "d30597cdecb8ccd3b56bb275cd86fcdc7a279f86eafa92ddc49f66512f0bff67"
SRC_URI += "file://0001-setup-don-t-use-setuptools-markdown.patch"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-keys \
    python3-pycryptodome \
    python3-setuptools \
"
