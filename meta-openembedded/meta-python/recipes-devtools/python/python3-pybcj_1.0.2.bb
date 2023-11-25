SUMMARY = "bcj filter library"
HOMEPAGE = "https://codeberg.org/miurahr/pybcj"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "c7f5bef7f47723c53420e377bc64d2553843bee8bcac5f0ad076ab1524780018"

inherit pypi python_setuptools_build_meta pypi

#PROVIDES = "python3-pybcj"

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-toml-native \
    ${PYTHON_PN}-wheel-native \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-importlib-metadata \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-compression \
"
