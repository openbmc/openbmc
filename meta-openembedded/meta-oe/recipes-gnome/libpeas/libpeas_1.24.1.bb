SUMMARY = "libpeas is a gobject-based plugins engine"
HOMEPAGE = "https://wiki.gnome.org/Projects/Libpeas"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

DEPENDS = "gtk+3"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc gtk-icon-cache

SRC_URI[archive.md5sum] = "bbecf334a7333d0a5d4d655ba38be9b4"
SRC_URI[archive.sha256sum] = "9c3acf7a567cbb4f8bf62b096e013f12c3911cc850c3fa9900cbd5aa4f6ec284"

PACKAGECONFIG[python3] = "-Dpython3=true,-Dpython3=false,python3-pygobject"

PACKAGES =+ "${PN}-demo ${PN}-python3"
FILES_${PN}-demo = " \
    ${bindir}/peas-demo \
    ${libdir}/peas-demo \
"

RDEPENDS_${PN}-python3 = "python3-pygobject"
FILES_${PN}-python3 = "${libdir}/libpeas-1.0/loaders/libpython3loader.so"
