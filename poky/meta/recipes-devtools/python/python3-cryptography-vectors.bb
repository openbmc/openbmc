SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4 \
                    file://LICENSE.APACHE;md5=4e168cce331e5c827d4c2b68a6200e1b \
                    file://LICENSE.BSD;md5=5ae30ba4123bc4f2fa49aa0b0dce887b"

# NOTE: Make sure to keep this recipe at the same version as python3-cryptography
#       Upgrade both recipes at the same time
require python3-cryptography-common.inc
SRC_URI[sha256sum] = "ff6a885265f484e8907277614b73e9c774b01658623ad763dde2858188e486b4"

PYPI_PACKAGE = "cryptography_vectors"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-cryptography \
"

BBCLASSEXTEND = "native nativesdk"
