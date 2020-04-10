DESRIPTION = "2D barcode scanner toolkit."
SECTION = "graphics"
LICENSE = "LGPL-2.1"

DEPENDS = "pkgconfig intltool-native libpng jpeg"

LIC_FILES_CHKSUM = "file://COPYING;md5=4015840237ca7f0175cd626f78714ca8"

PV = "0.10+git${SRCPV}"

#  iPhoneSDK-1.3.1 tag
SRCREV = "67003d2a985b5f9627bee2d8e3e0b26d0c474b57"
SRC_URI = "git://github.com/ZBar/Zbar \
           file://0001-make-relies-GNU-extentions.patch \
"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[x11] = "--with-x,-without-x,libxcb libx11 libsm libxau libxext libxv libice libxdmcp"

EXTRA_OECONF = "--without-imagemagick --without-qt --without-python --disable-video --without-gtk"

CPPFLAGS += "-Wno-error"

do_install_append() {
    #remove usr/bin if empty
    rmdir ${D}${bindir}
}
