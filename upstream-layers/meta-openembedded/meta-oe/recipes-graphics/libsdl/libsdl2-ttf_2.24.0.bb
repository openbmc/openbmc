SUMMARY = "Simple DirectMedia Layer truetype font library"
SECTION = "libs"
DEPENDS = "virtual/libsdl2 freetype"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=fbb0010b2f7cf6e8a13bcac1ef4d2455"

SRC_URI = "http://www.libsdl.org/projects/SDL_ttf/release/SDL2_ttf-${PV}.tar.gz"
SRC_URI[sha256sum] = "0b2bf1e7b6568adbdbc9bb924643f79d9dedafe061fa1ed687d1d9ac4e453bfd"

inherit cmake pkgconfig

S = "${UNPACKDIR}/SDL2_ttf-${PV}"

FILES:${PN} += "${datadir}/licenses"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'opengl', d)}"

# GL is only used for an example binary
PACKAGECONFIG[opengl] = "-DSDL2TTF_SAMPLES=ON,-DSDL2TTF_SAMPLES=OFF,virtual/egl"
