SUMMARY = "Cryptographic library for Python"
DESCRIPTION = "PyCryptodome is a self-contained Python package of low-level\
 cryptographic primitives."
HOMEPAGE = "http://www.pycryptodome.org"
LICENSE = "PD & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=775fe77831c03bf82f35abc0e230d4c1"

SRC_URI[md5sum] = "a2587b6381b0ef4eb960cea1e7507d04"
SRC_URI[sha256sum] = "c5dd29e9f1b733e74311bf95d0e544e91bd1d14bc0366e8f443562d8d9920b7d"

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
