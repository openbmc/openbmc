SUMMARY = "Python parsing module"
HOMEPAGE = "http://pyparsing.wikispaces.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=657a566233888513e1f07ba13e2f47f1"

SRC_URI[md5sum] = "e534c0ca755155823bf45fdd8d084922"
SRC_URI[sha256sum] = "1873c03321fc118f4e9746baf201ff990ceb915f433f23b395f5580d1840cb2a"

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
