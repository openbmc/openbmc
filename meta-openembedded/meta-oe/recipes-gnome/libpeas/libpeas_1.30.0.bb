SUMMARY = "libpeas is a gobject-based plugins engine"
HOMEPAGE = "https://wiki.gnome.org/Projects/Libpeas"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

DEPENDS = "gtk+3"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc gtk-icon-cache features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "0bf5562e9bfc0382a9dcb81f64340787542568762a3a367d9d90f6185898b9a3"

PACKAGECONFIG[python3] = "-Dpython3=true,-Dpython3=false,python3-pygobject"

PACKAGES =+ "${PN}-demo ${PN}-python3"
FILES:${PN}-demo = " \
    ${bindir}/peas-demo \
    ${libdir}/peas-demo \
"

RDEPENDS:${PN}-python3 = "python3-pygobject"
FILES:${PN}-python3 = "${libdir}/libpeas-1.0/loaders/libpython3loader.so"
