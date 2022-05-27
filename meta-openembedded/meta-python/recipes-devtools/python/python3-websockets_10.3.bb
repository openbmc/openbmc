SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78c2cc91e172ca96d6f8e4a76c739ec6"

inherit pypi setuptools3

SRC_URI[sha256sum] = "fc06cc8073c8e87072138ba1e431300e2d408f054b27047d047b549455066ff4"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
