SUMMARY = "MQTT version 3.1/3.1.1 client library"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eb48c6ee2cb9f5b8b9fe75e6f817bdfc"
SRCNAME = "paho-mqtt"

inherit pypi setuptools

RDEPENDS_${PN} = "\
               python-math \
               python-io \
               python-threading \
"

SRC_URI[md5sum] = "a6407b74eb5e5411e157be1de5c11366"
SRC_URI[sha256sum] = "0f7a629efe6e3a2c61b59d3550aa9f2c4529b5689a65fde45e6f1ac36b9a261e"
