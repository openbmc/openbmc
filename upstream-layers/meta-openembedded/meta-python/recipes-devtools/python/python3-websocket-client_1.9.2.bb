SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69c77173fe8892e9fdecd46557b957ad"

SRC_URI[sha256sum] = "0fcb57545848be86992e128218fd96dd87a6769ffdb1a968dff79632b85604d0"

PYPI_PACKAGE = "websocket_client"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "\
    python3-six \
    python3-logging \
"
