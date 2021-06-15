SUMMARY = "GNOME Structured File Library"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc7371b50816c96e145fa0f8ade8e24d \
                    file://COPYING.LIB;md5=61464cfe342798eeced82efe9ae55f63"

SECTION = "libs"

DEPENDS= "libxml2 bzip2 glib-2.0 zlib gnome-common-native"

inherit gnomebase gobject-introspection gettext gtk-doc

SRC_URI[archive.md5sum] = "20bf9933128210d7a9f920a34198d22f"
SRC_URI[archive.sha256sum] = "d188ebd3787b5375a8fd38ee6f761a2007de5e98fa0cf5623f271daa67ba774d"
SRC_URI += "file://0001-configure.ac-drop-a-copy-paste-of-introspection.m4-m.patch"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gdk-pixbuf] = "--with-gdk-pixbuf,--without-gdk-pixbuf,gdk-pixbuf"

EXTRA_OECONF = "\
    --with-bz2 \
"

FILES_${PN} += "${datadir}/thumbnailers"
