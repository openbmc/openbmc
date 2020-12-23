SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"

SRC_URI[md5sum] = "20976284d12ed99e31d38814be038ac1"
SRC_URI[sha256sum] = "785f06ffd0bbc73bdd69d0b164d72a7a7827c7c3bbf79ae9a235d7126afd98e4"

PYPI_PACKAGE = "cryptography_vectors"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-cryptography \
"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
