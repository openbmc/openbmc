RPITOOLS_DATE ?= "20210705"
SRCREV ?= "2e59fc67d465510179155973d2b959e50a440e47"
RPITOOLS_SRC_URI ?= "https://github.com/raspberrypi/tools/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-tools-${SRCREV}.tar.gz"
RPITOOLS_S ?= "${WORKDIR}/tools-${SRCREV}"

SRC_URI = "${RPITOOLS_SRC_URI}"
SRC_URI[sha256sum] = "77b0d2350d5f1d7003cbfa68db5318241caec3e1b5e499d65673ec6d987dcb1a"

PV = "${RPITOOLS_DATE}"
