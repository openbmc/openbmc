SUMMARY = "websocket client for python"
DESCRIPTION = "\
websocket-client module is WebSocket client for python. \
This provide the low level APIs for WebSocket. All APIs \
are the synchronous functions."
HOMEPAGE = "https://github.com/websocket-client/websocket-client"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c96ca6c1de8adc025adfada81d06fba5"

SRC_URI[sha256sum] = "3e2bf58191d4619b161389a95bdce84ce9e0b24eb8107e7e590db682c2d0ca81"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-six \
"
