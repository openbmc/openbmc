DESCRIPTION = "WebSocket client & server library, WAMP real-time framework"
HOMEPAGE = "http://crossbar.io/autobahn"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=49165a577911c4178e915dc26e2802a3"

SRC_URI[sha256sum] = "754c06a54753aeb7e8d10c5cbf03249ad9e2a1a32bca8be02865c6f00628a98c"

CVE_PRODUCT = "autobahn"

inherit pypi python_hatchling python_setuptools_build_meta

DEPENDS += " \
    python3-cffi-native \
"

RDEPENDS:${PN} += " \
    python3-twisted \
    python3-zopeinterface \
    python3-py-ubjson \
    python3-cbor2 \
    python3-u-msgpack-python \
    python3-lz4 \
    python3-snappy \
    python3-pyopenssl \
    python3-txaio \
    python3-six \
"
