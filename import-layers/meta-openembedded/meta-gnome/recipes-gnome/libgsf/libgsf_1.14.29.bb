SUMMARY = "GNOME Structured File Library"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc7371b50816c96e145fa0f8ade8e24d \
                    file://COPYING.LIB;md5=61464cfe342798eeced82efe9ae55f63"

SECTION = "libs"

DEPENDS= "libxml2 bzip2 glib-2.0 zlib intltool-native gnome-common-native"

inherit autotools pkgconfig gnome gconf gobject-introspection

SRC_URI += "file://0001-configure.ac-drop-a-copy-paste-of-introspection.m4-m.patch"

SRC_URI[archive.md5sum] = "3056b94bb3281dbc8311371bfc23cf72"
SRC_URI[archive.sha256sum] = "4d8bca33424eb711acdb6a060cb488b132063d699c4fa201db24c2c89c62529c"

GNOME_COMPRESS_TYPE = "xz"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gdk-pixbuf] = "--with-gdk-pixbuf,--without-gdk-pixbuf,gdk-pixbuf"

EXTRA_OECONF = "\
    --disable-gtk-doc \
    --with-bz2 \
"

RDEPENDS_${PN} = "gconf"

FILES_${PN} += "${datadir}/thumbnailers"
