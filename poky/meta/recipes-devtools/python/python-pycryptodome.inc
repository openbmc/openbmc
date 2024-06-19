SUMMARY = "Cryptographic library for Python"
DESCRIPTION = "PyCryptodome is a self-contained Python package of low-level\
 cryptographic primitives."
HOMEPAGE = "http://www.pycryptodome.org"
LICENSE = "PD & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=29242a70410a4eeff488a28164e7ab93"

inherit pypi

RDEPENDS:${PN} += " \
    python3-cffi \
    python3-ctypes \
    python3-io \
    python3-math \
"

RDEPENDS:${PN}-tests += " \
    python3-unittest \
"

PACKAGES =+ "${PN}-tests"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Crypto/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Crypto/SelfTest/__pycache__/ \
"

BBCLASSEXTEND = "native nativesdk"
