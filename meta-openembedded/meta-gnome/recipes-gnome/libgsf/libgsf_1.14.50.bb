SUMMARY = "GNOME Structured File Library"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc7371b50816c96e145fa0f8ade8e24d \
                    file://COPYING.LIB;md5=61464cfe342798eeced82efe9ae55f63"

SECTION = "libs"

DEPENDS= "libxml2 bzip2 glib-2.0 zlib gnome-common-native"

inherit gnomebase gobject-introspection gettext gtk-doc

SRC_URI[archive.sha256sum] = "6e6c20d0778339069d583c0d63759d297e817ea10d0d897ebbe965f16e2e8e52"
SRC_URI += "file://0001-configure.ac-drop-a-copy-paste-of-introspection.m4-m.patch"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gdk-pixbuf] = "--with-gdk-pixbuf,--without-gdk-pixbuf,gdk-pixbuf"

EXTRA_OECONF = "\
    --with-bz2 \
"

FILES:${PN} += "${datadir}/thumbnailers"
