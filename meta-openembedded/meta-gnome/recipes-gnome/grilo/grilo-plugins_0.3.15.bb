SUMMARY = "Grilo is a framework forsearching media content from various sources"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = " \
    glib-2.0-native \
    gperf-native \
    itstool-native \
    grilo \
    tracker \
    lua \
    liboauth \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gnome-help vala

SRC_URI += "file://0001-Avoid-running-trackertestutils.patch"
SRC_URI[archive.sha256sum] = "8518c3d954f93095d955624a044ce16a7345532f811d299dbfa1e114cfebab33"

FILES:${PN} += "${libdir}/grilo-0.3"
