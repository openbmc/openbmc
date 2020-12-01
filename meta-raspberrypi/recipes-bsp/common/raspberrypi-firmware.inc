RPIFW_DATE ?= "20201002"
SRCREV ?= "11e3c314bc2b64f7d862bac00ff3d9f42f3c5a50"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "b8c9f5a3e987f418f11526cbf59f6a6c8103e8f5e94bccffb9ef3fd7ae834f98"

PV = "${RPIFW_DATE}"
