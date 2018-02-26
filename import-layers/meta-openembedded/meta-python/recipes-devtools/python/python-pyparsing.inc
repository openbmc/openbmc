SUMMARY = "Python parsing module"
HOMEPAGE = "http://pyparsing.wikispaces.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fb46329938e6bc829b256e37d5c1e31a"

SRC_URI[md5sum] = "0214e42d63af850256962b6744c948d9"
SRC_URI[sha256sum] = "0832bcf47acd283788593e7a0f542407bd9550a55a8a8435214a1960e04bcb04"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native"
