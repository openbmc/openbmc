SUMMARY = "Client for MQTT protocol"
HOMEPAGE = "https://github.com/wialon/gmqtt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903f1792621a3b35ee546da75d139177"

SRC_URI[md5sum] = "af3a2c6c2f8e3c934b31159ffdce5fd6"
SRC_URI[sha256sum] = "b96bca8a54b8af057d4cc42a79f9e2b40cdbb5a2bfebbc5f05ee35575d3e3089"

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
