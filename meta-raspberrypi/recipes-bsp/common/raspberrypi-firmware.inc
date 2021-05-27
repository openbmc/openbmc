RPIFW_DATE ?= "20210225"
SRCREV ?= "5985247fb75681985547641d66196c77499f26b9"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "3e2c00e1473bd70e808134925e1b25cd765789d9f0e0683749135b124d835000"

PV = "${RPIFW_DATE}"
