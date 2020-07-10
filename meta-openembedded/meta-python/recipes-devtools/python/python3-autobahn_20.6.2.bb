DESCRIPTION = "WebSocket client & server library, WAMP real-time framework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97c0bda20ad1d845c6369c0e47a1cd98"

SRC_URI[md5sum] = "bcafb42ea58232308777a265d41c1c98"
SRC_URI[sha256sum] = "6ac6b6653b8d20d632b034adccf6a566154d4efbeaa23abf7c3995fd601e9a01"

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
