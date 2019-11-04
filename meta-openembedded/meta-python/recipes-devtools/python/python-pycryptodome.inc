SUMMARY = "Cryptographic library for Python"
DESCRIPTION = "PyCryptodome is a self-contained Python package of low-level\
 cryptographic primitives."
HOMEPAGE = "http://www.pycryptodome.org"
LICENSE = "PD & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=6dc0e2a13d2f25d6f123c434b761faba"

SRC_URI[md5sum] = "97ee98c95f350927bcc0e937976f17ee"
SRC_URI[sha256sum] = "dbeb08ad850056747aa7d5f33273b7ce0b9a77910604a1be7b7a6f2ef076213f"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-math \
"

RDEPENDS_${PN}-tests += " \
    ${PYTHON_PN}-unittest \
"

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/Crypto/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Crypto/SelfTest/__pycache__/ \
"

BBCLASSEXTEND = "native nativesdk"
