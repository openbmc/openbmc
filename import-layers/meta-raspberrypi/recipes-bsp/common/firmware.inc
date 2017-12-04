RPIFW_DATE ?= "20170405"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/1.${RPIFW_DATE}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-1.${RPIFW_DATE}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "ea82d14a7cd8cfae9b78e00d4e56bc71"
SRC_URI[sha256sum] = "2f4e5bddbac1372590db203002c35cbba3fb9d6172a93c314ee27bf05ae13bff"

PV = "${RPIFW_DATE}"

