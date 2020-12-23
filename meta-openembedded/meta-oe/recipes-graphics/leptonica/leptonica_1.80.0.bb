DESCRIPTION = "A pedagogically-oriented open source site containing software that is broadly useful for image processing and image analysis applications"
DEPENDS = "jpeg tiff libpng zlib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://leptonica-license.txt;md5=809b282cea2df68975fbe0ffe02b560f"

UPSTREAM_CHECK_URI = "https://github.com/DanBloomberg/leptonica/releases"

SRC_URI = "http://www.leptonica.org/source/leptonica-${PV}.tar.gz"
SRC_URI[md5sum] = "d640d684234442a84c9e8902f0b3ff36"
SRC_URI[sha256sum] = "ec9c46c2aefbb960fb6a6b7f800fe39de48343437b6ce08e30a8d9688ed14ba4"

EXTRA_OECONF += " \
    --without-libwebp \
"

PACKAGECONFIG ??= "giflib"
PACKAGECONFIG[openjpeg] = "--with-libopenjpeg,--without-libopenjpeg,openjpeg"
PACKAGECONFIG[giflib] = "--with-giflib,--without-giflib,giflib"

inherit autotools pkgconfig
