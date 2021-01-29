SUMMARY = "The xmlschema library is an implementation of XML Schema for Python (supports Python 3.6+)."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=47489cb18c469474afeb259ed1d4832f"

SRC_URI[sha256sum] = "f372005d08dfc231ce7f001c3a7a2c7f7ae138295349268bc551a9df3c700b82"

PYPI_PACKAGE = "xmlschema"
inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-elementpath-native \
"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-elementpath \
"

BBCLASSEXTEND = "native nativesdk"
