RPIFW_DATE ?= "20210527"
SRCREV ?= "1eb8921ae7158249894f89e8bb6c4bfb04ec7377"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "32fb5cc72703247622839c3cb4df2f369f91d2185770df867ac9758283bf5380"

PV = "${RPIFW_DATE}"
