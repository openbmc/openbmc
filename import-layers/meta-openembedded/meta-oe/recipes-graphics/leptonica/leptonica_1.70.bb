DESCRIPTION = "A pedagogically-oriented open source site containing software that is broadly useful for image processing and image analysis applications"
DEPENDS = "jpeg tiff libpng zlib giflib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://leptonica-license.txt;md5=20cdd3af097ca431b82004c691f406a6"

SRC_URI = "http://leptonica.com/source/leptonica-${PV}.tar.gz"
SRC_URI[md5sum] = "5ac2a31cf5b4f0e8f5a853a5266c42ef"
SRC_URI[sha256sum] = "d3d209a1f6d1f7a80119486b5011bc8c6627e582c927ab44ba33c37edb2cfba2"

EXTRA_OECONF += " \
    --without-libwebp \
"

inherit autotools pkgconfig
