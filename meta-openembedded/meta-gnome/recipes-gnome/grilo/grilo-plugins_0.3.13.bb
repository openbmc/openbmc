SUMMARY = "Grilo is a framework forsearching media content from various sources"
LICENSE = "LGPLv2.1"
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
SRC_URI[archive.sha256sum] = "1c4305d67364a930543836cc1982f30e946973b8ff6af3efe31d87709ab520f8"

FILES_${PN} += "${libdir}/grilo-0.3"
