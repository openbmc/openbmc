SUMMARY = "Library for building powerful interactive command lines in Python"
HOMEPAGE = "https://python-prompt-toolkit.readthedocs.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2cde7da89f0c1f3e49bf968d00d554f"

SRC_URI[sha256sum] = "941367d97fc815548822aa26c2a269fdc4eb21e9ec05fc5d447cf09bad5d75f0"

inherit pypi setuptools3

PYPI_PACKAGE = "prompt_toolkit"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-terminal \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-wcwidth \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-image \
    ${PYTHON_PN}-asyncio \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
