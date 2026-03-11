SUMMARY = "A library for handling the encrypted keyfiles used to store ethereum private keys."
HOMEPAGE = "https://github.com/ethereum/eth-keyfile"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6749008d847c14b9718949c2e24d5c0a"

SRC_URI[sha256sum] = "02e3c2e564c7403b92db3fef8ecae3d21123b15787daecd5b643a57369c530f9"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-keys \
    python3-pycryptodome \
    python3-setuptools \
"
