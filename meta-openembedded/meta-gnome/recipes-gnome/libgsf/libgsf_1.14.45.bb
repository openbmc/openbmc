SUMMARY = "GNOME Structured File Library"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc7371b50816c96e145fa0f8ade8e24d \
                    file://COPYING.LIB;md5=61464cfe342798eeced82efe9ae55f63"

SECTION = "libs"

DEPENDS= "libxml2 bzip2 glib-2.0 zlib intltool-native gnome-common-native"

inherit autotools pkgconfig gnomebase gobject-introspection

SRC_URI += "file://0001-configure.ac-drop-a-copy-paste-of-introspection.m4-m.patch"

SRC_URI[archive.md5sum] = "e45cc8aa9c49516d540b7d7307f755f1"
SRC_URI[archive.sha256sum] = "5cbc2c0f1dc44d202fa0c6e3a51e9f17b0c2deb8711ba650432bfde3180b69fa"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gdk-pixbuf] = "--with-gdk-pixbuf,--without-gdk-pixbuf,gdk-pixbuf"

EXTRA_OECONF = "\
    --disable-gtk-doc \
    --with-bz2 \
"

RDEPENDS_${PN} = "gconf"

FILES_${PN} += "${datadir}/thumbnailers"
