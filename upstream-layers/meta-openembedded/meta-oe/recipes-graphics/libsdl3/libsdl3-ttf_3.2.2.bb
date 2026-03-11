SUMMARY = "Simple DirectMedia Layer truetype font library"
SECTION = "libs"
DEPENDS = "libsdl3 freetype"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=fbb0010b2f7cf6e8a13bcac1ef4d2455"

SRC_URI = "http://www.libsdl.org/projects/SDL_ttf/release/SDL3_ttf-${PV}.tar.gz \
"
SRC_URI[sha256sum] = "63547d58d0185c833213885b635a2c0548201cc8f301e6587c0be1a67e1e045d"

S = "${UNPACKDIR}/SDL3_ttf-${PV}"

inherit cmake pkgconfig

FILES:${PN} += "${datadir}/licenses"
