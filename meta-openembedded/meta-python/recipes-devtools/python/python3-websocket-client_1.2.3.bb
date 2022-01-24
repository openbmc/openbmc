SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e401d789b93b47e65e852f16f2907aab"

SRC_URI[sha256sum] = "1315816c0acc508997eb3ae03b9d3ff619c9d12d544c9a9b553704b1cc4f6af5"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-logging \
"
