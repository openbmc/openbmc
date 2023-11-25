SUMMARY = "GNOME Structured File Library"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=61464cfe342798eeced82efe9ae55f63"

SECTION = "libs"

DEPENDS= "libxml2 bzip2 glib-2.0 zlib gnome-common-native"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gobject-introspection gettext gtk-doc

SRC_URI[archive.sha256sum] = "f0b83251f98b0fd5592b11895910cc0e19f798110b389aba7da1cb7c474017f5"
SRC_URI += "file://0001-configure.ac-drop-a-copy-paste-of-introspection.m4-m.patch"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gdk-pixbuf] = "--with-gdk-pixbuf,--without-gdk-pixbuf,gdk-pixbuf"

EXTRA_OECONF = "\
    --with-bz2 \
"

FILES:${PN} += "${datadir}/thumbnailers"
