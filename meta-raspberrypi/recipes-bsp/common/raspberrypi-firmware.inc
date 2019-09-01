RPIFW_DATE ?= "20190823"
SRCREV ?= "7163480fff007dc98978899b556dcf06f8a462c8"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "b10044aa637ed170eef2ca6c1dcc8aba"
SRC_URI[sha256sum] = "012bc542157d03d19c52dfb2ff9e828905d1991a8b33420f1a2e3730040c167f"

PV = "${RPIFW_DATE}"
