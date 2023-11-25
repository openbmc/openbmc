SUMMARY = "Multi volume file wrapper library"
HOMEPAGE = "https://codeberg.org/miurahr/multivolume"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "a0648d0aafbc96e59198d5c17e9acad7eb531abea51035d08ce8060dcad709d6"

inherit python_setuptools_build_meta pypi

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-toml-native \
    ${PYTHON_PN}-wheel-native \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-mmap \
"
