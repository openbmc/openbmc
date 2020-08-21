SUMMARY = "Client for MQTT protocol"
HOMEPAGE = "https://github.com/wialon/gmqtt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903f1792621a3b35ee546da75d139177"

SRC_URI[md5sum] = "9e26fec7d5b64afab63d6d82faacc208"
SRC_URI[sha256sum] = "86b3679de400b8068dfadf871ce063a7040ed5052d30cc323aed69430888b422"

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
