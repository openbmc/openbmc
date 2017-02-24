DESCRIPTION = "An extension that includes Bootstrap in your project, without any boilerplate code."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=3452e378337a5cc2092d32a140178d5a"

SRC_URI[md5sum] = "b8aedbe51f2bf511af955f2bc288cd8c"
SRC_URI[sha256sum] = "3a7b71e22596a6d559965e059958960e0bb671adc131537a79edb491a8f31714"

PYPI_PACKAGE = "Flask-Bootstrap"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dominate \
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-visitor \
    "
