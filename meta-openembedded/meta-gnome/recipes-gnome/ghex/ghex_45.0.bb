SUMMARY = "GHex - a hex editor for GNOME"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"


DEPENDS = " \
    desktop-file-utils-native  \
    glib-2.0-native \
    gtk4 \
    libadwaita \
"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

inherit gnomebase gsettings gtk-icon-cache gnome-help gettext gobject-introspection vala gi-docgen

SRC_URI += "file://0001-gtkhex-Local-variables-in-switch-statement-should-be.patch"
SRC_URI[archive.sha256sum] = "05cecc4561ca40b257c5db31da9f68d696133efc0ae427ed82fb985a986e840e"

FILES:${PN} += "${libdir} ${datadir}/metainfo"
