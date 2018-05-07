RPIFW_DATE ?= "20180619"
SRCREV ?= "748fb17992426bb29d99224b93cb962fefbdc833"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "02edb854e9b72499225f3f93d322fe12"
SRC_URI[sha256sum] = "07b2dc9d0779e15666e3ab596387e18cefc265d863c4bf6f8a0572b58a4fb793"

PV = "${RPIFW_DATE}"
