SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c96ca6c1de8adc025adfada81d06fba5"

SRC_URI[sha256sum] = "b68e4959d704768fa20e35c9d508c8dc2bbc041fd8d267c0d7345cffe2824568"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-six \
"
