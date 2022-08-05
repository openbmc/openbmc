DESCRIPTION = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e55731824cf9205cfabeab9a0600887"

SRC_URI[sha256sum] = "29adc2665447e5191d0e7c568fde78b21f9672d344281d0c6e1ab085429b22b6"

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
