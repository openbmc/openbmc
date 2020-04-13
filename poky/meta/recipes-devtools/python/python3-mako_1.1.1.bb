SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=943eb67718222db21d44a4ef1836675f"

PYPI_PACKAGE = "Mako"

inherit pypi setuptools3

SRC_URI[md5sum] = "2660a4916f2f63456e6885c727b7cd2f"
SRC_URI[sha256sum] = "2984a6733e1d472796ceef37ad48c26f4a984bb18119bb2dbc37a44d8f6e75a4"

RDEPENDS_${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-threading \
"

RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
