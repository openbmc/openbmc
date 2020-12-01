SUMMARY = "Client for MQTT protocol"
HOMEPAGE = "https://github.com/wialon/gmqtt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903f1792621a3b35ee546da75d139177"

SRC_URI[md5sum] = "8745f3b576e5247310276f0ef5c32f65"
SRC_URI[sha256sum] = "1285c428a5faf4c6aaac1e2ccb876383b4c7e087ef34ff7edb9f549a78cdebf1"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    python3-asyncio \
    python3-core \
    python3-datetime \
    python3-json \
    python3-logging \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
