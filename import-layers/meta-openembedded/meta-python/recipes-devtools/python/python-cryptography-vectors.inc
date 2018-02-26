SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"

DEPENDS += " \
    ${PYTHON_PN}-cryptography \
"

SRC_URI[md5sum] = "1db4c09062a3a8e1e2d1f5e3e4d752d7"
SRC_URI[sha256sum] = "beb831aa73663a224f4d7520483ed02da544533bb03b26ec07a5f9a0dd0941e1"

PYPI_PACKAGE = "cryptography_vectors"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
