SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"

DEPENDS += " \
    ${PYTHON_PN}-cryptography \
"

SRC_URI[md5sum] = "ef115b870cc8d8b9f0f4189b512bc170"
SRC_URI[sha256sum] = "78c4b4f3f84853ea5d038e2f53d355229dd8119fe9cf949c3e497c85c760a5ca"

PYPI_PACKAGE = "cryptography_vectors"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
