RPIFW_DATE ?= "20190220"
SRCREV ?= "ebf7b40cdc6effe7ffa3fd5195e8be01b94f80da"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "611077d343e3802496dd99118ca4e977"
SRC_URI[sha256sum] = "e911f6342ea8aa9380cae97c6f5ac4ddd370527265f23d5b5d4b2c6f679d4ba8"

PV = "${RPIFW_DATE}"
