RPIFW_DATE ?= "20191210"
SRCREV ?= "9d6be5b07e81bdfb9c4b9a560e90fbc7477fdc6e"
RPIFW_SRC_URI ?= "https://github.com/raspberrypi/firmware/archive/${SRCREV}.tar.gz"
RPIFW_S ?= "${WORKDIR}/firmware-${SRCREV}"

SRC_URI = "${RPIFW_SRC_URI}"
SRC_URI[md5sum] = "645e812765c8b4ca05d6cb47a1f67ab0"
SRC_URI[sha256sum] = "484d52caed909fcafbf593cc3e726ea44a9218db7f0aeec843b825797eb9b0fc"

PV = "${RPIFW_DATE}"
