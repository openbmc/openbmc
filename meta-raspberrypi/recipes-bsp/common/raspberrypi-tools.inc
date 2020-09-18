SRCREV ?= "b0c869bc929587a7e1d20a98e2dc828a24ca396a"
RPITOOLS_SRC_URI ?= "https://github.com/raspberrypi/tools/archive/${SRCREV}.tar.gz;downloadfilename=raspberrypi-tools-${SRCREV}.tar.gz"
RPITOOLS_S ?= "${WORKDIR}/tools-${SRCREV}"

SRC_URI = "${RPITOOLS_SRC_URI}"
SRC_URI[md5sum] = "fd999aad4424088b5e8746a9d946bb56"
SRC_URI[sha256sum] = "a0a8aff308d9302318442e6bcd2df14cbbb3b633f12ca6b9c43c4ab69bf45bb1"

PV = "0.0+git${SRCREV}"
