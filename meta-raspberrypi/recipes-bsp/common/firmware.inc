RPIFW_DATE ?= "20180924"
SRCREV ?= "5b49caa17e91d0e64024380119ad739bb201c674"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "51352972a029c6f47bde6d302b69440e"
SRC_URI[sha256sum] = "d1f9c58957dfe681fff7e1cf1eabb9e0f6fdc99720f1d059fb24a37750573310"


PV = "${RPIFW_DATE}"
