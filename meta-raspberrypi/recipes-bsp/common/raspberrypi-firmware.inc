RPIFW_DATE ?= "20200603"
SRCREV ?= "23548e550a757d368d3d5220373fe829b5961c42"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "acd99b86ab857b43d3e3a6bf53ee7d89"
SRC_URI[sha256sum] = "ea8accaecd4bd1ff5e1ca2c0cf4c182c829779d89a6fc4d64a058c6ddd6b654b"

PV = "${RPIFW_DATE}"
