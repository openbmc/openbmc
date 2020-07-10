SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"

SRC_URI[md5sum] = "657349b8de73959fc49ba15fd7346b58"
SRC_URI[sha256sum] = "99ac91c4d89572399b097240809af1b8f67f4bedf43398d290c9b2b3d9f491b4"

PYPI_PACKAGE = "cryptography_vectors"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-cryptography \
"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
