SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5070256738c06d2e59adbec1f4057dac"

inherit pypi setuptools3

SRC_URI[md5sum] = "9e8c6b3c70def4146d75fbb0f52bdfc7"
SRC_URI[sha256sum] = "da4d4fbe059b0453e726d6d993760065d69b823a27efc3040402a6fcfe6a1ed9"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-asyncio \
"
