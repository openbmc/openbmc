RPIFW_DATE ?= "20180817"
SRCREV ?= "53ac68e1eb02138f72a5056efed9dac9d3883a3f"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "6b3d901decf2fe4fad151e3ebb28cb62"
SRC_URI[sha256sum] = "3680de3ea9817b10fa8998b0dfa6811e58c96945474f4c782710dcd0a56c8cf0"


PV = "${RPIFW_DATE}"
