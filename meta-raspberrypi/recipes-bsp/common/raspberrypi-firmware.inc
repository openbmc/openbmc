RPIFW_DATE ?= "20191107"
SRCREV ?= "d737daa826adaab8679f83018c3cdc8783d49b59"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "002b6544826ce8f0d1176dd2e958022c"
SRC_URI[sha256sum] = "48817e60ca15098ff46d7942914fcbdc44e74c97aa3d58889e59a90901b8fc1a"

PV = "${RPIFW_DATE}"
