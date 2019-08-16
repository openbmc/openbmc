SUMMARY = "Python parsing module"
HOMEPAGE = "http://pyparsing.wikispaces.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=657a566233888513e1f07ba13e2f47f1"

SRC_URI[md5sum] = "46d02cbe0461fe0571d51649e6006ef5"
SRC_URI[sha256sum] = "6f98a7b9397e206d78cc01df10131398f1c8b8510a2f4d97d9abd82e1aacdd80"

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

BBCLASSEXTEND = "native nativesdk"
