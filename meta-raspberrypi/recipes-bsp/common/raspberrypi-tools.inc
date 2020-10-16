RPITOOLS_DATE ?= "20200803"
SRCREV ?= "0c39cb5b5ac9851312a38c54f5aea770d976de7a"
RPITOOLS_SRC_URI ?= "https://github.com/raspberrypi/tools/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-tools-${SRCREV}.tar.gz"
RPITOOLS_S ?= "${WORKDIR}/tools-${SRCREV}"

SRC_URI = "${RPITOOLS_SRC_URI}"
SRC_URI[sha256sum] = "ef56a891eef387d2278e6f9d20b462021faace14aceeb2048b5e5973069c25c0"

PV = "${RPITOOLS_DATE}"
