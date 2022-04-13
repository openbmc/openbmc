SUMMARY = "The xmlschema library is an implementation of XML Schema for Python (supports Python 3.6+)."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=47489cb18c469474afeb259ed1d4832f"

SRC_URI[sha256sum] = "be1eedce6a4b911fd3a7f4060d0811951820a13410e61f0454b30e9f4e7cf197"

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
