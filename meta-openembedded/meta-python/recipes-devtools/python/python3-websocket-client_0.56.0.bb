SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4c4a98fbc4836b81c8c64d6ecb01fc1"

SRC_URI[md5sum] = "89484bd5dac71123ae6a09b2f90fe62c"
SRC_URI[sha256sum] = "1fd5520878b68b84b5748bb30e592b10d0a91529d5383f74f4964e72b297fd3a"

PYPI_PACKAGE = "websocket_client"
inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-six \
"
