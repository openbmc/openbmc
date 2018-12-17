SUMMARY = "SDL graphics drawing primitives and other support functions"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d1de21f0b70830e299905eac3419084"

DEPENDS = "virtual/libsdl"

SRC_URI = "http://www.ferzkopp.net/Software/SDL_gfx-2.0/SDL_gfx-${PV}.tar.gz"
SRC_URI[md5sum] = "ea24ed4b82ff1304809c363494fa8e16"
SRC_URI[sha256sum] = "556eedc06b6cf29eb495b6d27f2dcc51bf909ad82389ba2fa7bdc4dec89059c0"

S = "${WORKDIR}/SDL_gfx-${PV}"

inherit autotools pkgconfig

EXTRA_OECONF += " \
    --disable-mmx \
"

