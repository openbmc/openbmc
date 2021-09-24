RPIFW_DATE ?= "20210914"
SRCREV ?= "d5d14f484cec739901a088a58eed62452b6dadeb"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "f5b3d9fa60afbc103b00bc7f5899ea49ef61d353b88624b42dbf7669446a44ac"

PV = "${RPIFW_DATE}"
