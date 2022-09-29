DESCRIPTION = "Simple DirectMedia Layer networking library."
SECTION = "libs/network"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING;md5=9cf3de2d872bf510f88eb20d06d700b5"

inherit autotools pkgconfig

DEPENDS = "libsdl"

SRC_URI = " \
  https://www.libsdl.org/projects/SDL_net/release/SDL_net-${PV}.tar.gz \
  file://libtool2.patch \
  file://am_foreign.patch \
"

S = "${WORKDIR}/SDL_net-${PV}"

SRC_URI[md5sum] = "20e64e61d65662db66c379034f11f718"
SRC_URI[sha256sum] = "5f4a7a8bb884f793c278ac3f3713be41980c5eedccecff0260411347714facb4"
