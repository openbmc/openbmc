SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1e01b26bacfc2232046c90a330332b3"

SRC_URI[sha256sum] = "8dfb715d8a992f5712fff8c843adae94e22b22a99b2c5e6b0ec4a1a981cc4e0d"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-logging \
"
