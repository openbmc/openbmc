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
SRC_URI[archive.sha256sum] = "686844b34ec73b24931ff6cc4f6033f0072947a6db60acdc7fb3eaf157a581c8"

FILES:${PN} += "${libdir}/grilo-0.3"
