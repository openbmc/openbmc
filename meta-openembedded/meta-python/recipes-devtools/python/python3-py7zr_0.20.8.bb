SUMMARY = "Pure Python 7-zip library"
HOMEPAGE = "https://py7zr.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "2a6b0db0441e63a2dd74cbd18f5d9ae7e08dc0e54685aa486361d0db6a0b4f78"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-toml-native \
    ${PYTHON_PN}-wheel-native \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-pycryptodomex \
    ${PYTHON_PN}-multivolumefile \
    ${PYTHON_PN}-pybcj \
    ${PYTHON_PN}-inflate64 \
    ${PYTHON_PN}-pyppmd \
    ${PYTHON_PN}-pyzstd \
    ${PYTHON_PN}-brotli \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-threading \
"
