SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ad5c6d36b3d0098b2f33a5ab69a9e750"

inherit pypi setuptools3

SRC_URI[md5sum] = "a37f0e22c3aa5f1cf12a6a3ae22b36ca"
SRC_URI[sha256sum] = "882a7266fa867a2ebb2c0baaa0f9159cabf131cf18c1b4270d79ad42f9208dc5"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-asyncio \
"
