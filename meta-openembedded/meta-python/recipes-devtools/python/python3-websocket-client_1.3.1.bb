SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b969e9612325987c823fc0737063ebc8"

SRC_URI[sha256sum] = "6278a75065395418283f887de7c3beafb3aa68dada5cacbe4b214e8d26da499b"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-logging \
"
