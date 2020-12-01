SUMMARY = "Cryptographic library for Python"
DESCRIPTION = "PyCryptodome is a self-contained Python package of low-level\
 cryptographic primitives."
HOMEPAGE = "http://www.pycryptodome.org"
LICENSE = "PD & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=6dc0e2a13d2f25d6f123c434b761faba"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-math \
"

RDEPENDS_${PN}-tests += " \
    ${PYTHON_PN}-unittest \
"

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Crypto/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Crypto/SelfTest/__pycache__/ \
"

BBCLASSEXTEND = "native nativesdk"
