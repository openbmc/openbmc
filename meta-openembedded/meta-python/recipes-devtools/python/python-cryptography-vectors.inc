SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"

DEPENDS += " \
    ${PYTHON_PN}-cryptography \
"

SRC_URI[md5sum] = "294d44f32885a6b97d5f9cdd7ffc73a3"
SRC_URI[sha256sum] = "356a2ded84ae379e556515eec9b68dd74957651a38465d10605bb9fbae280f15"

PYPI_PACKAGE = "cryptography_vectors"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
