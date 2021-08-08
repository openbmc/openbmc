SUMMARY = "An implementation of the WebSocket Protocol (RFC 6455)"
HOMEPAGE = "https://github.com/aaugustin/websockets"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78c2cc91e172ca96d6f8e4a76c739ec6"

inherit pypi setuptools3

SRC_URI[sha256sum] = "276d2339ebf0df4f45df453923ebd2270b87900eda5dfd4a6b0cfa15f82111c3"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
