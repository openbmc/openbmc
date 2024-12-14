SUMMARY = "SDL graphics drawing primitives and other support functions"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d1de21f0b70830e299905eac3419084"

DEPENDS = "libsdl"

SRC_URI = "http://www.ferzkopp.net/Software/SDL_gfx-2.0/SDL_gfx-${PV}.tar.gz"
SRC_URI[sha256sum] = "dfb15ac5f8ce7a4952dc12d2aed9747518c5e6b335c0e31636d23f93c630f419"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/sdlgfx/files/"

S = "${WORKDIR}/SDL_gfx-${PV}"

inherit autotools pkgconfig

EXTRA_OECONF += " \
    --disable-mmx \
"

