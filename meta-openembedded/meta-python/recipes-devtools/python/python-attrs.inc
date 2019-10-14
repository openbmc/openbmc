DESCRIPTION = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4ab25949a73fe7d4fdee93bcbdbf8ff"

SRC_URI[sha256sum] = "f913492e1663d3c36f502e5e9ba6cd13cf19d7fab50aa13239e420fef95e1396"
SRC_URI[md5sum] = "d5a6ee80beaa072bd2b3bcb6fee0d508"

inherit pypi

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-ctypes \
"

BBCLASSEXTEND = "native"
