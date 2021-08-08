RPIFW_DATE ?= "20210727"
SRCREV ?= "e83f41345103a7fec73ccc5a2970d1812de77cdf"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "e7bd4532ac913663b5a8bf2644277208f3fc48e8136ddc84dad7ce1b62e4c37d"

PV = "${RPIFW_DATE}"
