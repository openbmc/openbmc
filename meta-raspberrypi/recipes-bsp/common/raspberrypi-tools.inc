SRCREV ?= "4a335520900ce55e251ac4f420f52bf0b2ab6b1f"
RPITOOLS_SRC_URI ?= "https://github.com/raspberrypi/tools/archive/${SRCREV}.tar.gz"
RPITOOLS_S ?= "${WORKDIR}/tools-${SRCREV}"

SRC_URI = "${RPITOOLS_SRC_URI}"
SRC_URI[md5sum] = "9d9f3e4887d357cbc5601e61bc538fae"
SRC_URI[sha256sum] = "7dcb188f14845909573a7ec6522b7ba8de5d3373d06aab0e8852f4c8352d6541"

PV = "0.0+git${SRCREV}"
