DESCRIPTION = "WebSocket client & server library, WAMP real-time framework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97c0bda20ad1d845c6369c0e47a1cd98"

SRC_URI[md5sum] = "1ea21f5e751517a71f605eaaff7f0c38"
SRC_URI[sha256sum] = "86bbce30cdd407137c57670993a8f9bfdfe3f8e994b889181d85e844d5aa8dfb"

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
