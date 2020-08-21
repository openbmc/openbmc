DESCRIPTION = "A pedagogically-oriented open source site containing software that is broadly useful for image processing and image analysis applications"
DEPENDS = "jpeg tiff libpng zlib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://leptonica-license.txt;md5=34aa579294e8284b7b848c8d5d361e8f"

UPSTREAM_CHECK_URI = "https://github.com/DanBloomberg/leptonica/releases"

SRC_URI = "http://www.leptonica.org/source/leptonica-${PV}.tar.gz"
SRC_URI[md5sum] = "a545654b1dae7d29e2ea346b29095f84"
SRC_URI[sha256sum] = "045966c9c5d60ebded314a9931007a56d9d2f7a6ac39cb5cc077c816f62300d8"

EXTRA_OECONF += " \
    --without-libwebp \
"

PACKAGECONFIG ??= "giflib"
PACKAGECONFIG[openjpeg] = "--with-libopenjpeg,--without-libopenjpeg,openjpeg"
PACKAGECONFIG[giflib] = "--with-giflib,--without-giflib,giflib"

inherit autotools pkgconfig
