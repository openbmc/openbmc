SUMMARY = "Client for MQTT protocol"
HOMEPAGE = "https://github.com/wialon/gmqtt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903f1792621a3b35ee546da75d139177"

SRC_URI[md5sum] = "9388ec09b6536c4e68c5ac5c31de3dc9"
SRC_URI[sha256sum] = "c12b2d7d5a90f3304b7291b1d9d21df47e228dfb4ff990e965008fdd1a55ce60"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    python3-asyncio \
    python3-core \
    python3-datetime \
    python3-json \
    python3-logging \
    python3-netclient \
    python3-typing \
"

BBCLASSEXTEND = "native nativesdk"
