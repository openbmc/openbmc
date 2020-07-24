RPIFW_DATE ?= "20200713"
SRCREV ?= "7e74bcb4f9706f36f752d1c3d3164628ccf2aae5"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "46a19d68b81f388443394492dd6a873c"
SRC_URI[sha256sum] = "f987cafcbc72179493673191e3e4aa8c1f18eccf871546be5d26156abbf8eff1"

PV = "${RPIFW_DATE}"
