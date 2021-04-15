RPIFW_DATE ?= "20210326"
SRCREV ?= "20081d8e86119e95e516526700be62049850c01a"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "00bd758a1242f069682e1a98250362b45a8887aed98ee61dd50439e9a78fe463"

PV = "${RPIFW_DATE}"
