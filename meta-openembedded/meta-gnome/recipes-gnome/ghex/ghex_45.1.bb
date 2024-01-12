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

SRC_URI[archive.sha256sum] = "fb2b0823cd16249edbeaee8302f9bd5005e0150368b35f1e47c26680cacac2fa"

FILES:${PN} += "${libdir} ${datadir}/metainfo"
