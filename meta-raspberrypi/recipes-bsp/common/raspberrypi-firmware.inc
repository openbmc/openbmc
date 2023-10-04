RPIFW_DATE ?= "20230509~buster"

RPIFW_SRC_URI ?= "https://archive.raspberrypi.com/debian/pool/main/r/raspberrypi-firmware/raspberrypi-firmware_1.${RPIFW_DATE}.orig.tar.xz"
RPIFW_S ?= "${WORKDIR}/raspberrypi-firmware-1.${RPIFW_DATE}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "1d9eb83111826b708f461101766fd2000d45f1c171ad573936d000f623ca8098"

PV = "${RPIFW_DATE}"
