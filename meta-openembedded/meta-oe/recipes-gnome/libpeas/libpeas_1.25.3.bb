SUMMARY = "libpeas is a gobject-based plugins engine"
HOMEPAGE = "https://wiki.gnome.org/Projects/Libpeas"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

DEPENDS = "gtk+3"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc gtk-icon-cache

SRC_URI[archive.md5sum] = "08bfff8f9688cf630dcb0f950617661f"
SRC_URI[archive.sha256sum] = "1c9bbb29740c29cd6e1dd0c9964722ff08cd5e6f68f1b5c135bc391a6ce97639"

PACKAGECONFIG[python3] = "-Dpython3=true,-Dpython3=false,python3-pygobject"

PACKAGES =+ "${PN}-demo ${PN}-python3"
FILES_${PN}-demo = " \
    ${bindir}/peas-demo \
    ${libdir}/peas-demo \
"

RDEPENDS_${PN}-python3 = "python3-pygobject"
FILES_${PN}-python3 = "${libdir}/libpeas-1.0/loaders/libpython3loader.so"
