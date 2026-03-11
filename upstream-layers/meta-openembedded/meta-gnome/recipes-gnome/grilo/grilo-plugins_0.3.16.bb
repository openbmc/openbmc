SUMMARY = "Grilo is a framework forsearching media content from various sources"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = " \
    glib-2.0-native \
    gperf-native \
    itstool-native \
    grilo \
    liboauth \
"

inherit gnomebase gnome-help vala

SRC_URI += "file://0001-Avoid-running-trackertestutils.patch"
SRC_URI[archive.sha256sum] = "fe6f4dbe586c6b8ba2406394e202f22d009d642a96eb3a54f32f6a21d084cdcb"

# gnome-online-accounts requires opengl by default, so lua-factory PACKAGECONFIG relies on opengl by default.
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'gobject-introspection-data', 'tracker', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'lua-factory', '', d)} \
                   "
PACKAGECONFIG[tracker] = "-Denable-tracker3=yes,-Denable-tracker3=no,tinysparql,localsearch"
PACKAGECONFIG[lua-factory] = "-Denable-lua-factory=yes,-Denable-lua-factory=no,lua libarchive json-glib gnome-online-accounts"

FILES:${PN} += "${libdir}/grilo-0.3"
