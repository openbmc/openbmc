RPIFW_DATE ?= "20190212"
SRCREV ?= "83977fe3b6ef54c1d29c83b0a778d330f523441f"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "5ccdb5447cbdd3ee0158a514f7b76cb9"
SRC_URI[sha256sum] = "9a34ccc4a51695a33206cc6c8534f615ba5a30fcbce5fa3add400ecc6b80ad8a"


PV = "${RPIFW_DATE}"
