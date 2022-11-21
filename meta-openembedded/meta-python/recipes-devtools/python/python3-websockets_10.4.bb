SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78c2cc91e172ca96d6f8e4a76c739ec6"

inherit pypi setuptools3

SRC_URI[sha256sum] = "eef610b23933c54d5d921c92578ae5f89813438fded840c2e9809d378dc765d3"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
