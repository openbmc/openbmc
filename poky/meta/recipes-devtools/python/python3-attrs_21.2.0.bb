DESCRIPTION = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4ab25949a73fe7d4fdee93bcbdbf8ff"

SRC_URI[sha256sum] = "ef6aaac3ca6cd92904cdd0d83f629a15f18053ec84e6432106f7a4d04ae4f5fb"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-ctypes \
"
RDEPENDS:${PN}:class-nativesdk += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
