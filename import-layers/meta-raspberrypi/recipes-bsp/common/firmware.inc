RPIFW_DATE ?= "20171029"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/1.${RPIFW_DATE}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-1.${RPIFW_DATE}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "4d27c1888a7bab3097471906e7b4a319"
SRC_URI[sha256sum] = "46ce28c8d87ef22bdcc57ac1836ca3f04d1ec6f46580ff5a30bf76b3c0822117"
PV = "${RPIFW_DATE}"

