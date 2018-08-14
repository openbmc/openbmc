SUMMARY = "World timezone definitions, modern and historical"
HOMEPAGE = "http://pythonhosted.org/pytz"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4878a915709225bceab739bdc2a18e8d"

inherit pypi

SRC_URI[md5sum] = "f054437920c895dd14a4509fabafe029"
SRC_URI[sha256sum] = "c06425302f2cf668f1bba7a0a03f3c1d34d4ebeef2c72003da308b3947c7f749"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
