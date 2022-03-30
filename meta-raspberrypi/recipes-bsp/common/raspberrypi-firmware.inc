RPIFW_DATE ?= "20211007"

RPIFW_SRC_URI ?= "https://archive.raspberrypi.com/debian/pool/main/r/raspberrypi-firmware/raspberrypi-firmware_1.${RPIFW_DATE}.orig.tar.xz"
RPIFW_S ?= "${WORKDIR}/raspberrypi-firmware-1.${RPIFW_DATE}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "cb08f4679ab9928f89b2b73bbd92dace84dd692e1e1722f373eebd350f0c058f"

PV = "${RPIFW_DATE}"
