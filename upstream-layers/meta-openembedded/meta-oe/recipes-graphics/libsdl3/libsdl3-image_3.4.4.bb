SUMMARY = "Simple DirectMedia Layer image library v2"
SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=df8f4d887d3997f6e9cf81bb7f43dbf7"

DEPENDS = "tiff zlib libpng jpeg libsdl3 libwebp libjxl"

SRC_URI = "git://github.com/libsdl-org/SDL_image.git;protocol=https;branch=release-3.4.x;tag=release-${PV}"
SRCREV = "bec9134a26c7d0f31b36d6083c25296e04cabff5"

EXTRA_OECMAKE += "-DSDLIMAGE_JXL=ON"

PACKAGECONFIG ?= ""
PACKAGECONFIG[av1] = "-DSDLIMAGE_AVIF=ON,-DSDLIMAGE_AVIF=OFF,libavif"

inherit cmake pkgconfig

FILES:${PN} += "${datadir}/licenses"
