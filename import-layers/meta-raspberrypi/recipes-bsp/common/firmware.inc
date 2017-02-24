RPIFW_DATE ?= "20161125"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/1.${RPIFW_DATE}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-1.${RPIFW_DATE}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "a9281d2869e2d7673a83d41bacfa66c2"
SRC_URI[sha256sum] = "744e050630621c70991c91e0ee8663dc2f579e743583ca762b13b00bc75859bc"

PV = "${RPIFW_DATE}"

