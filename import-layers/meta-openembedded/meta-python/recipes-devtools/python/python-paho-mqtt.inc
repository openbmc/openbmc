SUMMARY = "MQTT version 3.1/3.1.1 client library"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eb48c6ee2cb9f5b8b9fe75e6f817bdfc"
SRCNAME = "paho-mqtt"

inherit pypi
SRC_URI[md5sum] = "241150b3fcb920ddca4d33181f3238b1"
SRC_URI[sha256sum] = "9100a6aa706ab699d414ec02705a21eb66f436184691d0bf1f2a85a6213c6c1f"

RDEPENDS_${PN} = "\
               ${PYTHON_PN}-math \
               ${PYTHON_PN}-io \
               ${PYTHON_PN}-threading \
"
