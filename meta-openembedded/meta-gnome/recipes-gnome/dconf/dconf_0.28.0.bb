SUMMARY = "configuation database system"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"
SECTION = "x11/gnome"

SRC_URI[archive.md5sum] = "81faa8e68e5ea71ff0ee75050fc0759c"
SRC_URI[archive.sha256sum] = "61d3b3865ef58b729c3b39aa0979f886c014aa8362f93dcfc74bf5648ed9c742"

DEPENDS = "dbus glib-2.0 intltool-native"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings bash-completion vala

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/gio/modules/*.so \
"
