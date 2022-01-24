SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78c2cc91e172ca96d6f8e4a76c739ec6"

inherit pypi setuptools3

SRC_URI[sha256sum] = "181d2b25de5a437b36aefedaf006ecb6fa3aa1328ec0236cdde15f32f9d3ff6d"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
