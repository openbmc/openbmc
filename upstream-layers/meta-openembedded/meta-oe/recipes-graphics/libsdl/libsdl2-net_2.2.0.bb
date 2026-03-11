DESCRIPTION = "Simple DirectMedia Layer networking library."
SECTION = "libs/network"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=68a088513da90254b2fbe664f42af315"

SRC_URI = " \
  https://www.libsdl.org/projects/SDL_net/release/SDL2_net-${PV}.tar.gz \
"
S = "${UNPACKDIR}/SDL2_net-${PV}"

inherit cmake pkgconfig

DEPENDS = "virtual/libsdl2"

SRC_URI[sha256sum] = "4e4a891988316271974ff4e9585ed1ef729a123d22c08bd473129179dc857feb"

FILES:${PN} += "${datadir}/licenses"
