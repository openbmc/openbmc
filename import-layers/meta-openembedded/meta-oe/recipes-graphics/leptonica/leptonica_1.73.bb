DESCRIPTION = "A pedagogically-oriented open source site containing software that is broadly useful for image processing and image analysis applications"
DEPENDS = "jpeg tiff libpng zlib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://leptonica-license.txt;md5=20cdd3af097ca431b82004c691f406a6"

SRC_URI = "http://leptonica.com/source/leptonica-${PV}.tar.gz"
SRC_URI[md5sum] = "092cea2e568cada79fff178820397922"
SRC_URI[sha256sum] = "19e4335c674e7b78af9338d5382cc5266f34a62d4ce533d860af48eaa859afc1"

EXTRA_OECONF += " \
    --without-libwebp \
"

PACKAGECONFIG ??= "giflib"
PACKAGECONFIG[openjpeg] = "--with-libopenjpeg,--without-libopenjpeg,openjpeg"
PACKAGECONFIG[giflib] = "--with-giflib,--without-giflib,giflib"

inherit autotools pkgconfig
