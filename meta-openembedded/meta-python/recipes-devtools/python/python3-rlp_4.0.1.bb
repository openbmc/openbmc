SUMMARY = "A Python implementation of Recursive Length Prefix encoding (RLP)."
HOMEPAGE = "https://github.com/ethereum/pyrlp"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=00854fa80a84236706b11f47f23e61e7"

SRC_URI[sha256sum] = "bcefb11013dfadf8902642337923bd0c786dc8a27cb4c21da6e154e52869ecb1"

inherit pypi setuptools3

DEPENDS += "python3-pip-native"

RDEPENDS:${PN} += " \
    python3-eth-utils \
    python3-typing-extensions \
"
