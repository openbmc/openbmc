SUMMARY = "Python support for YAML"
HOMEPAGE = "http://www.pyyaml.org"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a76b4c69bfcf82313bbdc0393b04438a"
DEPENDS = "libyaml ${PYTHON_PN}-cython-native ${PYTHON_PN}"

PYPI_PACKAGE = "PyYAML"
inherit pypi

SRC_URI[md5sum] = "20f87ab421b0271dbf371dc5c1cddb5c"
SRC_URI[sha256sum] = "01adf0b6c6f61bd11af6e10ca52b7d4057dd0be0343eb9283c878cf3af56aee4"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
