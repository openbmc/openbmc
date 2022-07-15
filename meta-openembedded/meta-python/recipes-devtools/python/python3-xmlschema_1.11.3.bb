SUMMARY = "The xmlschema library is an implementation of XML Schema for Python (supports Python 3.6+)."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=47489cb18c469474afeb259ed1d4832f"

SRC_URI[sha256sum] = "28a135028f7ab1e0c934fc0c6717a66b2dc5f166d123dfe6ce61afc671ad113f"

PYPI_PACKAGE = "xmlschema"
inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-elementpath-native \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-elementpath \
    ${PYTHON_PN}-modules \
"

BBCLASSEXTEND = "native nativesdk"
