DESCRIPTION = "WebSocket client & server library, WAMP real-time framework"
HOMEPAGE = "http://crossbar.io/autobahn"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97c0bda20ad1d845c6369c0e47a1cd98"

SRC_URI[sha256sum] = "bf7a9d302a34d0f719d43c57f65ca1f2f5c982dd6ea0c11e1e190ef6f43710fe"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-twisted \
    ${PYTHON_PN}-zopeinterface \
    ${PYTHON_PN}-py-ubjson \
    ${PYTHON_PN}-cbor2 \
    ${PYTHON_PN}-u-msgpack-python \
    ${PYTHON_PN}-lz4 \
    ${PYTHON_PN}-snappy \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-txaio \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
