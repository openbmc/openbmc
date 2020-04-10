SUMMARY = "Python support for YAML"
HOMEPAGE = "http://www.pyyaml.org"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a76b4c69bfcf82313bbdc0393b04438a"
DEPENDS = "libyaml ${PYTHON_PN}-cython-native ${PYTHON_PN}"

PYPI_PACKAGE = "PyYAML"
inherit pypi

SRC_URI[md5sum] = "d7e45e932d6e6649ac574a1ed77b4269"
SRC_URI[sha256sum] = "c0ee8eca2c582d29c3c2ec6e2c4f703d1b7f1fb10bc72317355a746057e7346c"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
