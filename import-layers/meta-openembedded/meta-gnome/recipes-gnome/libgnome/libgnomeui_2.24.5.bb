DESCRIPTION = "GNOME User Interface Library"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"

SECTION = "x11/gnome/libs"
DEPENDS = "libgnome libgnomecanvas libbonoboui libgnome-keyring intltool-native gnome-common-native"

inherit gnome

FILES_${PN} += "${libdir}/gtk-2.0/*/filesystems/lib*.so \
                ${libdir}/libglade/*/lib*.so \
                ${datadir}/pixmaps/gnome-about-logo.png"
FILES_${PN}-dev += "${libdir}/gtk-2.0/*/filesystems/*.la ${libdir}/gtk-2.0/*/filesystems/*.a ${libdir}/libglade/*/*.la"
FILES_${PN}-staticdev += "${libdir}/libglade/*/*.a"

SRC_URI += " \
    file://0001-suppress-string-format-literal-warning-to-fix-build-.patch \
    file://gnome-stock-pixbufs.h \
    file://no-pixbuf-csource.patch \
"
SRC_URI[archive.md5sum] = "d4bb506b1916015323928faab5aa708b"
SRC_URI[archive.sha256sum] = "ae352f2495889e65524c979932c909f4629a58e64290fb0c95333373225d3c0f"
GNOME_COMPRESS_TYPE="bz2"

EXTRA_OECONF = "--disable-gtk-doc"

do_configure_prepend() {
    install -m 0644 ${WORKDIR}/gnome-stock-pixbufs.h ${S}/libgnomeui/pixmaps/gnome-stock-pixbufs.h
}

LDFLAGS += "-lm"
