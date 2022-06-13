SUMMARY = "A Python implementation of Recursive Length Prefix encoding (RLP)."
HOMEPAGE = "https://github.com/ethereum/pyrlp"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=00854fa80a84236706b11f47f23e61e7"

SRC_URI[sha256sum] = "63b0465d2948cd9f01de449d7adfb92d207c1aef3982f20310f8009be4a507e8"
SRC_URI += "file://0001-setup-don-t-use-setuptools-markdown.patch"

inherit pypi setuptools3

DEPENDS += "python3-pip-native"

RDEPENDS:${PN} += "python3-eth-utils"
