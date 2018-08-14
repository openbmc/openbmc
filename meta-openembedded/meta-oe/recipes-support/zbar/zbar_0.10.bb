DESRIPTION = "2D barcode scanner toolkit."
SECTION = "graphics"
LICENSE = "LGPL-2.1"

DEPENDS = "pkgconfig intltool-native libpng jpeg"

LIC_FILES_CHKSUM = "file://COPYING;md5=42bafded1b380c6fefbeb6c5cd5448d9"

SRC_URI = "${SOURCEFORGE_MIRROR}/${PN}/${P}.tar.bz2 \
           file://0001-undefine-__va_arg_pack.patch \
           file://0001-make-relies-GNU-extentions.patch \
"

SRC_URI[md5sum] = "0fd61eb590ac1bab62a77913c8b086a5"
SRC_URI[sha256sum] = "234efb39dbbe5cef4189cc76f37afbe3cfcfb45ae52493bfe8e191318bdbadc6"

inherit autotools pkgconfig

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[x11] = "--with-x,-without-x,libxcb libx11 libsm libxau libxext libxv libice libxdmcp"

EXTRA_OECONF = " --without-imagemagick --without-qt --without-python --disable-video --without-gtk"

do_install_append() {
    #remove usr/bin if empty
    rmdir ${D}${bindir}
}
