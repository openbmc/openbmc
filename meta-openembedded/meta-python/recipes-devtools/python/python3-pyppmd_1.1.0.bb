SUMMARY = "PPMd compression/decompression library"
HOMEPAGE = "https://pyppmd.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "1d38ce2e4b7eb84b53bc8a52380b94f66ba6c39328b8800b30c2b5bf31693973"

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-toml-native \
    ${PYTHON_PN}-wheel-native \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-importlib-metadata \
"
