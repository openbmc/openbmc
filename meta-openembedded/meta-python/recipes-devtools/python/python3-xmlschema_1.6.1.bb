SUMMARY = "The xmlschema library is an implementation of XML Schema for Python (supports Python 3.6+)."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=47489cb18c469474afeb259ed1d4832f"

SRC_URI[sha256sum] = "d02b82bb4fce3af09495b85e6ada5ce0cfdaa865b09b28ecc0f5ab81fad7e328"

PYPI_PACKAGE = "xmlschema"
inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-elementpath-native \
"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-elementpath \
"

BBCLASSEXTEND = "native nativesdk"
