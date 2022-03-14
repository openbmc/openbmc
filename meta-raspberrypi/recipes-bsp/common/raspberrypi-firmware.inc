RPIFW_DATE ?= "20211007"
SRCREV ?= "0c4fc71befd228419a225c84d316cabbda8633a7"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "2502930165328be3b292389b1f7e25365c724000d6c404b63a5a0ba7af120aa7"

PV = "${RPIFW_DATE}"
