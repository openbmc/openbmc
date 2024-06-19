SUMMARY = "Client for MQTT protocol"
HOMEPAGE = "https://github.com/wialon/gmqtt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903f1792621a3b35ee546da75d139177"

SRC_URI[sha256sum] = "ddd1fdc1c6ae604e74377cf70e99f067e579c03c1c71a6acd494e199e93b7fa4"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-asyncio \
    python3-core \
    python3-datetime \
    python3-json \
    python3-logging \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
