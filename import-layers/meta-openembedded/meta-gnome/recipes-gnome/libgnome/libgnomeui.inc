DESCRIPTION = "GNOME User Interface Library"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"

SECTION = "x11/gnome/libs"
DEPENDS = "libgnome libgnomecanvas libbonoboui libgnome-keyring intltool-native"

inherit gnome 

FILES_${PN} += "${libdir}/gtk-2.0/*/filesystems/lib*.so \
                ${libdir}/libglade/*/lib*.so \
                ${datadir}/pixmaps/gnome-about-logo.png"
FILES_${PN}-dev += "${libdir}/gtk-2.0/*/filesystems/*.la ${libdir}/gtk-2.0/*/filesystems/*.a ${libdir}/libglade/*/*.la"
FILES_${PN}-staticdev += "${libdir}/libglade/*/*.a"
FILES_${PN}-dbg += "${libdir}/gtk-2.0/*/filesystems/.debug/ ${libdir}/libglade/*/.debug/"

SRC_URI += "file://gnome-stock-pixbufs.h file://no-pixbuf-csource.patch"

EXTRA_OECONF = "--disable-gtk-doc"

do_configure_prepend() {
    install -m 0644 ${WORKDIR}/gnome-stock-pixbufs.h ${S}/libgnomeui/pixmaps/gnome-stock-pixbufs.h
}

