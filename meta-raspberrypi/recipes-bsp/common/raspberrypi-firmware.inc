RPIFW_DATE ?= "20210205"
SRCREV ?= "7d91570f20378afc9414107dccdad70705a8a342"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "2cc9bf2cbcab8283db2eb53ed2a49372b70fe76538994c32f6d261e2da3e9ff4"

PV = "${RPIFW_DATE}"
