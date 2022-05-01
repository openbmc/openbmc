RPIFW_DATE ?= "20220331"

RPIFW_SRC_URI ?= "https://archive.raspberrypi.com/debian/pool/main/r/raspberrypi-firmware/raspberrypi-firmware_1.${RPIFW_DATE}.orig.tar.xz"
RPIFW_S ?= "${WORKDIR}/raspberrypi-firmware-1.${RPIFW_DATE}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "8758f10797bd52a7373cc5b39bd46d0d9f882d501ccb9535a72a3fe8a8d329c3"

PV = "${RPIFW_DATE}"
