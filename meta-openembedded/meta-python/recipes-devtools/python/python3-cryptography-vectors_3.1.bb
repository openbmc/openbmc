SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"

SRC_URI[md5sum] = "1ba5b0743e2876e909f7cfc28b744f27"
SRC_URI[sha256sum] = "2660bc3a720d3955341803195ae5352a236c5f26e01a178c44fc5ee1f0ef1182"

PYPI_PACKAGE = "cryptography_vectors"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-cryptography \
"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
