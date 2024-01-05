SUMMARY = "Client for MQTT protocol"
HOMEPAGE = "https://github.com/wialon/gmqtt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903f1792621a3b35ee546da75d139177"

SRC_URI[sha256sum] = "b4623d3e32ba266e96c04b0871bb057d05414371c1d279550648b390e6ba7577"

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
