SUMMARY = "libpeas is a gobject-based plugins engine"
HOMEPAGE = "https://wiki.gnome.org/Projects/Libpeas"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

DEPENDS = "gtk+3"

GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gobject-introspection gi-docgen gtk-icon-cache features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
# FIXME: When upgrading to libpeas 2, g-i is no longer needed.
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

SRC_URI = "https://download.gnome.org/sources/libpeas/1.38/libpeas-${PV}.tar.xz;name=archive"
SRC_URI += "file://0001-Remove-builddir-and-srcdir-paths-from-test-binaries.patch"
SRC_URI[archive.sha256sum] = "e82fd328adcac1aba34b64136bdfcbbacf2b3258a8bc4e5f480a72502a611ae9"
S = "${UNPACKDIR}/libpeas-${PV}"

PACKAGECONFIG[python3] = "-Dpython3=true,-Dpython3=false,python3-pygobject"

PACKAGES =+ "${PN}-demo ${PN}-python3"
FILES:${PN}-demo = " \
    ${bindir}/peas-demo \
    ${libdir}/peas-demo \
"

RDEPENDS:${PN}-python3 = "python3-pygobject"
FILES:${PN}-python3 = "${libdir}/libpeas-1.0/loaders/libpython3loader.so"
