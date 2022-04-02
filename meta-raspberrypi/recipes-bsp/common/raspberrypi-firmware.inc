RPIFW_DATE ?= "20220328"

RPIFW_SRC_URI ?= "https://archive.raspberrypi.com/debian/pool/main/r/raspberrypi-firmware/raspberrypi-firmware_1.${RPIFW_DATE}.orig.tar.xz"
RPIFW_S ?= "${WORKDIR}/raspberrypi-firmware-1.${RPIFW_DATE}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "8ab5949b9da1e2a2da18acc203946698737efe3e5d98286dcb7a8b0d0e92564e"

PV = "${RPIFW_DATE}"
