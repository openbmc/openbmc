DESCRIPTION = "Modern password hashing for your software and your servers."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f7bb094c7232b058c7e9f2e431f389c"

DEPENDS = "python-cffi-native"

SRC_URI[md5sum] = "7348b2c361e23cf205701bba7652d789"
SRC_URI[sha256sum] = "0309a4a72bd1dc314279cf1ee14e277227732f14c9b63ab96715654e13fe9321"

inherit pypi setuptools

RDEPENDS_${PN} = "\
    python-cffi \
    python-six \
"
