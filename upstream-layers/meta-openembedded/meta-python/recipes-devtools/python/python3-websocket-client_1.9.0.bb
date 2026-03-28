SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8d4a5e03977c68fad62beee8185704e"

SRC_URI[sha256sum] = "9e813624b6eb619999a97dc7958469217c3176312b3a16a4bd1bc7e08a46ec98"

PYPI_PACKAGE = "websocket_client"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    python3-six \
    python3-logging \
"
