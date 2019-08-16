RPIFW_DATE ?= "20190718"
SRCREV ?= "d36bde339b067bf13b610fd9741d6351c1dafc38"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "273fe147e3edc92ce38a0ff6bf49e3b9"
SRC_URI[sha256sum] = "f266df66ce92e7726e894c0c870f0d8e7257434f102ba77bd60640970b689aa3"

PV = "${RPIFW_DATE}"
