RPITOOLS_DATE ?= "20201008"
SRCREV ?= "fc0e73c13865450e95edd046200e42a6e52d8256"
RPITOOLS_SRC_URI ?= "https://github.com/raspberrypi/tools/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-tools-${SRCREV}.tar.gz"
RPITOOLS_S ?= "${WORKDIR}/tools-${SRCREV}"

SRC_URI = "${RPITOOLS_SRC_URI}"
SRC_URI[sha256sum] = "05217b942150830225e8ee04a8f16b8ecc2ffbbe5dd815541b15333f783e805e"

PV = "${RPITOOLS_DATE}"
