SUMMARY = "the blessed package to manage your versions by scm tags"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[md5sum] = "86ec1eb9ec02ba018b097787e98897c5"
SRC_URI[sha256sum] = "5bdf21a05792903cafe7ae0c9501182ab52497614fa6b1750d9dbae7b60c1a87"

PYPI_PACKAGE = "setuptools_scm"
inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-py \
    ${PYTHON_PN}-setuptools \
"
RDEPENDS_${PN}_class-native = "\
    ${PYTHON_PN}-setuptools-native \
"

BBCLASSEXTEND = "native nativesdk"
