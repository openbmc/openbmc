SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1bb21fa2d2f7a534c884b990430a6863"

PYPI_PACKAGE = "Mako"

inherit pypi

SRC_URI[md5sum] = "a94d376078dda65f834ea5049a81ebb5"
SRC_URI[sha256sum] = "7165919e78e1feb68b4dbe829871ea9941398178fa58e6beedb9ba14acf63965"

RDEPENDS_${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-threading \
"

RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
