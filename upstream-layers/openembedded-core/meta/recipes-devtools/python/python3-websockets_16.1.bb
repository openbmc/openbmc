SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51924a6af4495b8cfaee1b1da869b6f4"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "299468cbe42e2b9981134c7c51d99387d8a7bf562b00183b3eec53f882846dad"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = " \
    python3-asyncio \
"
