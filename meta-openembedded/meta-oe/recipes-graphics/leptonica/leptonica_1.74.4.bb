DESCRIPTION = "A pedagogically-oriented open source site containing software that is broadly useful for image processing and image analysis applications"
DEPENDS = "jpeg tiff libpng zlib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://leptonica-license.txt;md5=34aa579294e8284b7b848c8d5d361e8f"

SRC_URI = "http://www.leptonica.org/source/leptonica-${PV}.tar.gz"
SRC_URI[md5sum] = "4f32be9bd2e2c142ba018037ab5d746f"
SRC_URI[sha256sum] = "29c35426a416bf454413c6fec24c24a0b633e26144a17e98351b6dffaa4a833b"

EXTRA_OECONF += " \
    --without-libwebp \
"

PACKAGECONFIG ??= "giflib"
PACKAGECONFIG[openjpeg] = "--with-libopenjpeg,--without-libopenjpeg,openjpeg"
PACKAGECONFIG[giflib] = "--with-giflib,--without-giflib,giflib"

inherit autotools pkgconfig
