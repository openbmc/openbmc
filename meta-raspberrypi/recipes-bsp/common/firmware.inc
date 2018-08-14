RPIFW_DATE ?= "20180417"
SRCREV ?= "5db8e4e1c63178e200d6fbea23ed4a9bf4656658"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "6bce52f22a7a005a9bcdb9912b132590"
SRC_URI[sha256sum] = "84568c4e7f5b0deee4cd76e7c0b48315885f983d6131d8148ef8af8438328a36"

PV = "${RPIFW_DATE}"
