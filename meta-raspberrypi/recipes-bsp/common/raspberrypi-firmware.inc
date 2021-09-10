RPIFW_DATE ?= "20210819"
SRCREV ?= "25e2b597ebfb2495eab4816a276758dcc6ea21f1"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "abecc797c057ae57663eb54f2f0f5af79fb317bf873d2b3345f333848a5da3ee"

PV = "${RPIFW_DATE}"
