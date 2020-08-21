SUMMARY = "Library for building powerful interactive command lines in Python"
HOMEPAGE = "https://python-prompt-toolkit.readthedocs.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2cde7da89f0c1f3e49bf968d00d554f"

SRC_URI[md5sum] = "e50cb328715b338480d8d29dda54ff5d"
SRC_URI[sha256sum] = "7630ab85a23302839a0f26b31cc24f518e6155dea1ed395ea61b42c45941b6a6"

inherit pypi setuptools3

PYPI_PACKAGE = "prompt_toolkit"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-terminal \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-wcwidth \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-image \
"

BBCLASSEXTEND = "native nativesdk"
