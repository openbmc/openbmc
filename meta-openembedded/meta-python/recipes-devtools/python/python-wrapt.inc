SUMMARY = "A Python module for decorators, wrappers and monkey patching."
HOMEPAGE = "http://wrapt.readthedocs.org/"
LICENSE = "BSD"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61c27930012e86b3dc7e36f92e83c4d8"

SRC_URI[md5sum] = "e1346f31782d50401f81c2345b037076"
SRC_URI[sha256sum] = "d4d560d479f2c21e1b5443bbd15fe7ec4b37fe7e53d335d3b9b0a7b1226fe3c6"

inherit pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"
