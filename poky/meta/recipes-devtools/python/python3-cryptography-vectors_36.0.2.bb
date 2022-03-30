SUMMARY = "Test vectors for the cryptography package."
HOMEPAGE = "https://cryptography.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4 \
                    file://LICENSE.APACHE;md5=4e168cce331e5c827d4c2b68a6200e1b \
                    file://LICENSE.BSD;md5=5ae30ba4123bc4f2fa49aa0b0dce887b"

# NOTE: Make sure to keep this recipe at the same version as python3-cryptography
#       Upgrade both recipes at the same time

SRC_URI[sha256sum] = "2a7924449a03025faf7f8754a6f4200001cab210734a6ce6ac1f6bdacd3a68e4"

PYPI_PACKAGE = "cryptography_vectors"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-cryptography \
"

do_install:append () {
	# Remove the sha256 checksum lines for pycache files
	sed ${D}${PYTHON_SITEPACKAGES_DIR}/cryptography_vectors-${PV}.dist-info/RECORD -e '/__pycache__/d' -i
}

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
