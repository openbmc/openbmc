SUMMARY = "libpeas is a gobject-based plugins engine"
HOMEPAGE = "https://wiki.gnome.org/Projects/Libpeas"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

DEPENDS = "gtk+3"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gtk-doc gtk-icon-cache features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI += " \
    file://add_missing_locale_include.patch \
    file://0001-Do-not-build-tests-when-introspection-is-disabled-mi.patch \
"

SRC_URI[archive.md5sum] = "f7723bf8433b7984121157e1e9a629b5"
SRC_URI[archive.sha256sum] = "a976d77e20496479a8e955e6a38fb0e5c5de89cf64d9f44e75c2213ee14f7376"

PACKAGECONFIG[python3] = "-Dpython3=true,-Dpython3=false,python3-pygobject"

PACKAGES =+ "${PN}-demo ${PN}-python3"
FILES_${PN}-demo = " \
    ${bindir}/peas-demo \
    ${libdir}/peas-demo \
"

RDEPENDS_${PN}-python3 = "python3-pygobject"
FILES_${PN}-python3 = "${libdir}/libpeas-1.0/loaders/libpython3loader.so"
