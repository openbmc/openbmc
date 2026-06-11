DESCRIPTION = "Simple DirectMedia Layer networking library."
SECTION = "libs/network"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=036a54229112040a743509a86b30c80c"

SRC_URI = " \
  https://www.libsdl.org/projects/SDL_net/release/SDL2_net-${PV}.tar.gz"
S = "${UNPACKDIR}/SDL2_net-${PV}"

inherit cmake pkgconfig

DEPENDS = "virtual/libsdl2"

SRC_URI[sha256sum] = "9cbca2527feb3f1a622d48ba65cc7dee9b1e3f2c55ceafb7d7720bb058aafb30"

FILES:${PN} += "${datadir}/licenses"
