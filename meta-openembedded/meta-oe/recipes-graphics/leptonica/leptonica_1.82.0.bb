DESCRIPTION = "A pedagogically-oriented open source site containing software that is broadly useful for image processing and image analysis applications"
DEPENDS = "jpeg tiff libpng zlib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://leptonica-license.txt;md5=809b282cea2df68975fbe0ffe02b560f"

UPSTREAM_CHECK_URI = "https://github.com/DanBloomberg/leptonica/releases"

SRC_URI = "http://www.leptonica.org/source/leptonica-${PV}.tar.gz"
SRC_URI[sha256sum] = "155302ee914668c27b6fe3ca9ff2da63b245f6d62f3061c8f27563774b8ae2d6"

EXTRA_OECONF += " \
    --without-libwebp \
"

PACKAGECONFIG ??= "giflib"
PACKAGECONFIG[openjpeg] = "--with-libopenjpeg,--without-libopenjpeg,openjpeg"
PACKAGECONFIG[giflib] = "--with-giflib,--without-giflib,giflib"

inherit autotools pkgconfig
