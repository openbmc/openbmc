SUMMARY = "A Python implementation of Recursive Length Prefix encoding (RLP)."
HOMEPAGE = "https://github.com/ethereum/pyrlp"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=00854fa80a84236706b11f47f23e61e7"

SRC_URI[sha256sum] = "be07564270a96f3e225e2c107db263de96b5bc1f27722d2855bd3459a08e95a9"

inherit pypi setuptools3

DEPENDS += "python3-pip-native"

RDEPENDS:${PN} += " \
    python3-eth-utils \
    python3-typing-extensions \
"
