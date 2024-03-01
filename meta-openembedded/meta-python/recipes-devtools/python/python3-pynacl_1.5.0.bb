SUMMARY = "Python binding to the Networking and Cryptography (NaCl) library"
DESCRIPTION = "Python binding to the Networking and Cryptography (NaCl) library"
HOMEPAGE = "https://github.com/pyca/pynacl"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8cc789b082b3d97e1ccc5261f8594d3f"

SRC_URI[sha256sum] = "8ac7448f09ab85811607bdd21ec2464495ac8b7c66d146bf545b0f08fb9220ba"

PYPI_PACKAGE = "PyNaCl"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-cffi-native \
    libsodium \
"

do_compile:prepend() {
    export SODIUM_INSTALL=system
}

do_install:prepend() {
    export SODIUM_INSTALL=system
}

RDEPENDS:${PN} = "\
    python3-six \
    python3-cffi \
    libsodium \
"

RPROVIDES:${PN} = "python3-nacl"

# in meta-virtualization layer
#
RCONFLICTS:${PN} = "python3-nacl"

BBCLASSEXTEND = "native nativesdk"
