DESCRIPTION = "Self-service finite-state machines for the programmer on the go"
HOMEPAGE = "https://github.com/glyph/Automat"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ad213bcca81688e94593e5f60c87477"

SRC_URI[md5sum] = "d6cef9886b037b8857bfbc686f3ae30a"
SRC_URI[sha256sum] = "7979803c74610e11ef0c0d68a2942b152df52da55336e0c9d58daf1831cbdf33"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

SRC_URI_append = " \
    file://0001-setup.py-remove-the-dependency-on-m2r.patch \
"

PYPI_PACKAGE = "Automat"
inherit pypi setuptools3

RDEPENDS_${PN} += "\
   ${PYTHON_PN}-attrs \
   ${PYTHON_PN}-six \
"
