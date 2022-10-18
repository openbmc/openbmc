RPIFW_DATE ?= "20220830"

RPIFW_SRC_URI ?= "https://archive.raspberrypi.com/debian/pool/main/r/raspberrypi-firmware/raspberrypi-firmware_1.${RPIFW_DATE}.orig.tar.xz"
RPIFW_S ?= "${WORKDIR}/raspberrypi-firmware-1.${RPIFW_DATE}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "2b27e4b3c4d2664a0a1d0dd8602bd80ea41dd006eb0ad9c67d7b659c9c8bb4e5"

PV = "${RPIFW_DATE}"
