SUMMARY = "The xmlschema library is an implementation of XML Schema for Python (supports Python 3.6+)."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=47489cb18c469474afeb259ed1d4832f"

SRC_URI[sha256sum] = "c4c72b457865f46394bb23525b4e233d6537d9e97b8c6144a6cda2d5ef0d6688"

PYPI_PACKAGE = "xmlschema"
inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-elementpath-native \
"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-elementpath \
"

BBCLASSEXTEND = "native nativesdk"
