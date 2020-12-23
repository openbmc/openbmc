SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4c4a98fbc4836b81c8c64d6ecb01fc1"

SRC_URI[md5sum] = "8061820da5e1de26a6a1a6996d4eebd5"
SRC_URI[sha256sum] = "d735b91d6d1692a6a181f2a8c9e0238e5f6373356f561bb9dc4c7af36f452010"

PYPI_PACKAGE = "websocket_client"
inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-six \
"
