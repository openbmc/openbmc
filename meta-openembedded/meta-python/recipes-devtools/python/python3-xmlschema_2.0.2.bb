SUMMARY = "The xmlschema library is an implementation of XML Schema for Python (supports Python 3.6+)."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=47489cb18c469474afeb259ed1d4832f"

SRC_URI[sha256sum] = "ce915696b3a819fe0f986824517b9281dbd5626fd2d213363fab40f34edf05bd"

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
