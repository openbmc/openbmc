DESCRIPTION = "libgee is a collection library providing GObject-based interfaces \
and classes for commonly used data structures."
HOMEPAGE = "http://live.gnome.org/Libgee"
SECTION = "libs"
DEPENDS = "glib-2.0"

BBCLASSEXTEND = "native"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

PE = "1"

inherit autotools vala pkgconfig gobject-introspection
do_configure_prepend() {
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
    for i in ${MACROS}; do
        rm -f m4/$i
    done
}

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"
SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libgee/${SHRT_VER}/${BP}.tar.xz"
SRC_URI[md5sum] = "d224dca55bb909f6730f40cc267337be"
SRC_URI[sha256sum] = "bb2802d29a518e8c6d2992884691f06ccfcc25792a5686178575c7111fea4630"
