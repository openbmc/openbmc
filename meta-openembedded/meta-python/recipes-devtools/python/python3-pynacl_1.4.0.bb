SUMMARY = "Python binding to the Networking and Cryptography (NaCl) library"
DESCRIPTION = "Python binding to the Networking and Cryptography (NaCl) library"
HOMEPAGE = "https://github.com/pyca/pynacl"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8cc789b082b3d97e1ccc5261f8594d3f"

SRC_URI[md5sum] = "8c6c57893327a694c72510fb620e4744"
SRC_URI[sha256sum] = "54e9a2c849c742006516ad56a88f5c74bf2ce92c9f67435187c3c5953b346505"

PYPI_PACKAGE = "PyNaCl"

inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-wheel-native \
    ${PYTHON_PN}-cffi-native \
    libsodium \
"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-cffi \
    libsodium \
"

do_compile_prepend() {
    export SODIUM_INSTALL=system
}

do_install_prepend() {
    export SODIUM_INSTALL=system
}
