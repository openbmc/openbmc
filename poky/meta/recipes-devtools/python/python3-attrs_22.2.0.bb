DESCRIPTION = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e55731824cf9205cfabeab9a0600887"

SRC_URI[sha256sum] = "c9227bfc2f01993c03f68db37d1d15c9690188323c067c641f1a35ca58185f99"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:class-target += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-ctypes \
"
RDEPENDS:${PN}:class-nativesdk += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
