RPIFW_SRCREV ?= "390f53ed0fd79df274bdcc81d99e09fa262f03ab"
RPIFW_DATE ?= "20160622"
RPIFW_SRC_URI ?= "git://github.com/raspberrypi/firmware.git;protocol=git;branch=master"
RPIFW_S ?= "${WORKDIR}/git"

SRC_URI = "${RPIFW_SRC_URI}"
SRCREV = "${RPIFW_SRCREV}"
PV = "${RPIFW_DATE}"
