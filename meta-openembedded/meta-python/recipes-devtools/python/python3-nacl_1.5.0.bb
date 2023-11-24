SUMMARY = "Python binding to the Networking and Cryptography (NaCl) library"
HOMEPAGE = "https://github.com/pyca/pynacl"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8cc789b082b3d97e1ccc5261f8594d3f"

PYPI_PACKAGE = "PyNaCl"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8ac7448f09ab85811607bdd21ec2464495ac8b7c66d146bf545b0f08fb9220ba"

DEPENDS += "\
    libsodium \
    python3-cffi-native \
    python3-pip-native \
"

RDEPENDS:${PN}:class-target += " \
    python3-cffi \
    python3-pycparser \
"

export SODIUM_INSTALL = "system"

BBCLASSEXTEND = "native"