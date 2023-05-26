SUMMARY = "The xmlschema library is an implementation of XML Schema for Python (supports Python 3.6+)."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ab20f8e337bea2e6874f372edfd12c0"

SRC_URI[sha256sum] = "c2d583f7d07c6bac157d075889d15c128f34afdc79e4f70b4fb3c6adedc59bfe"

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
