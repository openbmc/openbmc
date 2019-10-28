RPIFW_DATE ?= "20191021"
SRCREV ?= "ed238d3329e39a68bde03bbc2dfc293c0c3dc93a"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "d18066531018a972ed927a6cc136cfff"
SRC_URI[sha256sum] = "82048cf88f4f50f7cf83e34d93923ceb5147885761d33ee5f4dc980b4a7a426a"

PV = "${RPIFW_DATE}"
