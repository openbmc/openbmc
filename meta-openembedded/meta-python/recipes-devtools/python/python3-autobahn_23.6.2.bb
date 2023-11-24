DESCRIPTION = "WebSocket client & server library, WAMP real-time framework"
HOMEPAGE = "http://crossbar.io/autobahn"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3e2c2c2cc2915edc5321b0e6b1d3f5f8"

SRC_URI[sha256sum] = "ec9421c52a2103364d1ef0468036e6019ee84f71721e86b36fe19ad6966c1181"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
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
