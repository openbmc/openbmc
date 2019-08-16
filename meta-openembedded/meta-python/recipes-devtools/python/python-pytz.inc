SUMMARY = "World timezone definitions, modern and historical"
HOMEPAGE = "http://pythonhosted.org/pytz"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4878a915709225bceab739bdc2a18e8d"

inherit pypi

SRC_URI[md5sum] = "8c21963449c3a793aa61ef122e171516"
SRC_URI[sha256sum] = "26c0b32e437e54a18161324a2fca3c4b9846b74a8dccddd843113109e1116b32"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
