SUMMARY = "Simple DirectMedia Layer image library v2"
SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=fbb0010b2f7cf6e8a13bcac1ef4d2455"

DEPENDS = "tiff zlib libpng jpeg virtual/libsdl2 libwebp"

SRC_URI = "http://www.libsdl.org/projects/SDL_image/release/SDL2_image-${PV}.tar.gz"
SRC_URI[sha256sum] = "393f5efb50536ec13ca4f4affb69cc9966d3c3f969e6c5e701faddf9f9785381"

S = "${UNPACKDIR}/SDL2_image-${PV}"

inherit cmake pkgconfig

FILES:${PN} += "${datadir}/licenses"
