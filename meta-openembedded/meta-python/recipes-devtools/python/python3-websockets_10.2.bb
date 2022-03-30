SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78c2cc91e172ca96d6f8e4a76c739ec6"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8351c3c86b08156337b0e4ece0e3c5ec3e01fcd14e8950996832a23c99416098"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
