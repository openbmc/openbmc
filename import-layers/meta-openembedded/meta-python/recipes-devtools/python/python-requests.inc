DESCRIPTION = "Python HTTP for Humans."
HOMEPAGE = "http://python-requests.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9bb3515869c0f426cb8441c899ae7f5"

SRC_URI[md5sum] = "ad5f9c47b5c5dfdb28363ad7546b0763"
SRC_URI[sha256sum] = "5acf980358283faba0b897c73959cecf8b841205bb4b2ad3ef545f46eae1a133"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-ndg-httpsclient \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-pyasn1 \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-urllib3 \
"

BBCLASSEXTEND = "native nativesdk"
