SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=943eb67718222db21d44a4ef1836675f"

PYPI_PACKAGE = "Mako"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8195c8c1400ceb53496064314c6736719c6f25e7479cd24c77be3d9361cddc27"

RDEPENDS_${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-threading \
"

RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
