SUMMARY = "GNOME Structured File Library"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc7371b50816c96e145fa0f8ade8e24d \
                    file://COPYING.LIB;md5=61464cfe342798eeced82efe9ae55f63"

SECTION = "libs"

DEPENDS= "libxml2 bzip2 glib-2.0 zlib gnome-common-native"

inherit gnomebase gobject-introspection gettext gtk-doc

SRC_URI[archive.sha256sum] = "ff86d7f1d46dd0ebefb7bd830a74a41db64362b987bf8853fff6ab4c1132b837"
SRC_URI += "file://0001-configure.ac-drop-a-copy-paste-of-introspection.m4-m.patch"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gdk-pixbuf] = "--with-gdk-pixbuf,--without-gdk-pixbuf,gdk-pixbuf"

EXTRA_OECONF = "\
    --with-bz2 \
"

FILES:${PN} += "${datadir}/thumbnailers"
