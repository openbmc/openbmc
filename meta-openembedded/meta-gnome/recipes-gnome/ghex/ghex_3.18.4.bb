SUMMARY = "GHex - a hex editor for GNOME"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
"

inherit gnomebase gsettings gtk-icon-cache gnome-help gettext upstream-version-is-even

SRC_URI[archive.md5sum] = "7e6ed808766bc18285bdc6999bdf0f15"
SRC_URI[archive.sha256sum] = "c2d9c191ff5bce836618779865bee4059db81a3a0dff38bda3cc7a9e729637c0"

FILES_${PN} += "${datadir}/metainfo"
