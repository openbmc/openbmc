RPIFW_DATE ?= "20200911"
SRCREV ?= "a490197f0672d948860b2b807884ae65eabc4d4f"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-firmware-${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[sha256sum] = "be06015688f415fa1b87850d51a4188a766f2a618001f70e7c69b07cbdf9d6ab"

PV = "${RPIFW_DATE}"
