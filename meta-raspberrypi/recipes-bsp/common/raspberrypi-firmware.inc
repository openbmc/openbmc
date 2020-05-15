RPIFW_DATE ?= "20200504"
SRCREV ?= "7eff9f6774bb43bfd61e749a0b45ffddc98c2311"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "05cd161dca5a6f02684794960913e04c"
SRC_URI[sha256sum] = "77ad450dd7cabb58ca04a18fd704844df6e642374346cf006a07edca46615af1"

PV = "${RPIFW_DATE}"
