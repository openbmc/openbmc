SUMMARY = "World timezone definitions, modern and historical"
HOMEPAGE = "http://pythonhosted.org/pytz"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4878a915709225bceab739bdc2a18e8d"

inherit pypi

SRC_URI[md5sum] = "8b2860a161bfb0a6165798b1a2d8c40c"
SRC_URI[sha256sum] = "d747dd3d23d77ef44c6a3526e274af6efeb0a6f1afd5a69ba4d5be4098c8e141"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
