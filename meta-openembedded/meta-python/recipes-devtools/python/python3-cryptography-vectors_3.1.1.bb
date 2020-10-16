SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"

SRC_URI[md5sum] = "fbc43f8b155b52f9ed163d3555ea5e83"
SRC_URI[sha256sum] = "5529c516194122fec7db1682812e2314ddff4221fdf993169118f9c0d291e2f6"

PYPI_PACKAGE = "cryptography_vectors"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-cryptography \
"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
