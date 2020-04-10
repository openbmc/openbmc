SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ad5c6d36b3d0098b2f33a5ab69a9e750"

inherit pypi setuptools3

SRC_URI[md5sum] = "f12d7f31fe8d0b3e65c12f845bcd0ad8"
SRC_URI[sha256sum] = "5c65d2da8c6bce0fca2528f69f44b2f977e06954c8512a952222cea50dad430f"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-asyncio \
"
