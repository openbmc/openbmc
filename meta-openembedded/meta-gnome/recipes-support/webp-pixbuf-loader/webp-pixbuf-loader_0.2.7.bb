SUMMARY = "WebP GDK Pixbuf Loader library"
HOMEPAGE = "https://github.com/aruiz/webp-pixbuf-loader"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL-2;md5=0d90e6d44bcf69014bfae649c75aa6ca"

DEPENDS = " \
    gdk-pixbuf \
    libwebp \
"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dupdate_cache=true"

SRC_URI = "git://github.com/aruiz/webp-pixbuf-loader.git;protocol=https;branch=mainline"

S = "${WORKDIR}/git"
SRCREV = "52232e4ba282b2fed68e8fcb4b5d45ed0eaa4ed3"

FILES:${PN} = " \
    ${datadir}/thumbnailers/webp-pixbuf.thumbnailer \
    ${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/libpixbufloader-webp.so \
"
