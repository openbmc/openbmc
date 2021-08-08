SUMMARY = "Client for MQTT protocol"
HOMEPAGE = "https://github.com/wialon/gmqtt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903f1792621a3b35ee546da75d139177"

SRC_URI[sha256sum] = "7ab7a226ab13d75f7bc34a1422da73658ce4cac86708bf55f92daf7c5f44165a"

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
