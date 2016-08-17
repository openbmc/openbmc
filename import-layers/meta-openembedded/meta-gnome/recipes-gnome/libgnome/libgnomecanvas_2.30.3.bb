SUMMARY = "A powerful object-oriented display"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"
SECTION = "x11/gnome/libs"

inherit gnomebase gtk-doc

# intltool to provide IT_PROG_INTLTOOL
# configure: line 12654: syntax error near unexpected token `0.35.0'
# configure: line 12654: `IT_PROG_INTLTOOL(0.35.0)'
DEPENDS = "gtk+ libglade libart-lgpl xineramaproto intltool-native"

SRC_URI[archive.md5sum] = "ffcbb719c671ff5cd86e59aeba8d0b92"
SRC_URI[archive.sha256sum] = "859b78e08489fce4d5c15c676fec1cd79782f115f516e8ad8bed6abcb8dedd40"
GNOME_COMPRESS_TYPE="bz2"

FILES_${PN} += "${libdir}/libglade/*/libcanvas.so"
FILES_${PN}-dbg += "${libdir}/libglade/*/.debug/"
FILES_${PN}-dev += "${libdir}/libglade/*/libcanvas*.la"
FILES_${PN}-staticdev += "${libdir}/libglade/*/libcanvas*.a"
