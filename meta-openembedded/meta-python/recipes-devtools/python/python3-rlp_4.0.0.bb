SUMMARY = "A Python implementation of Recursive Length Prefix encoding (RLP)."
HOMEPAGE = "https://github.com/ethereum/pyrlp"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=00854fa80a84236706b11f47f23e61e7"

SRC_URI[sha256sum] = "61a5541f86e4684ab145cb849a5929d2ced8222930a570b3941cf4af16b72a78"

inherit pypi setuptools3

DEPENDS += "python3-pip-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-eth-utils \
    ${PYTHON_PN}-typing-extensions \
"
