SUMMARY = "Client for MQTT protocol"
HOMEPAGE = "https://github.com/wialon/gmqtt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903f1792621a3b35ee546da75d139177"

SRC_URI[md5sum] = "f763f12c5e6ba6f0fa0ec4a6dc8c3fd9"
SRC_URI[sha256sum] = "3b3c12cb62bbc3ee0bd81da3d9fbd78c5414098aaf58236c3663edfeda5237e0"

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
