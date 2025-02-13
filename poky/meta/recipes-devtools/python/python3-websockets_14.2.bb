SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51924a6af4495b8cfaee1b1da869b6f4"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "5059ed9c54945efb321f097084b4c7e52c246f2c869815876a69d1efc4ad6eb5"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = " \
    python3-asyncio \
"
