DESCRIPTION = "An extension that includes Bootstrap in your project, without any boilerplate code."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=a03749709f06118a17349deb5a210619"

SRC_URI[md5sum] = "e40d50f5c5b6438c1c6200a6f2871f81"
SRC_URI[sha256sum] = "cb08ed940183f6343a64e465e83b3a3f13c53e1baabb8d72b5da4545ef123ac8"

PYPI_PACKAGE = "Flask-Bootstrap"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dominate \
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-visitor \
    "
