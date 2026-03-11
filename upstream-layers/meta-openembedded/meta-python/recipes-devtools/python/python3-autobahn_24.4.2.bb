DESCRIPTION = "WebSocket client & server library, WAMP real-time framework"
HOMEPAGE = "http://crossbar.io/autobahn"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=588502cb4ffc65da2b26780d6baa5a40"

SRC_URI[sha256sum] = "a2d71ef1b0cf780b6d11f8b205fd2c7749765e65795f2ea7d823796642ee92c9"

inherit pypi setuptools3

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
