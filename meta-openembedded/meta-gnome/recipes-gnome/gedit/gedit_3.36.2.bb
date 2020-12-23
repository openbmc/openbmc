SUMMARY = "GNOME editor"
SECTION = "x11/gnome"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    gdk-pixbuf-native \
    gtk+3 \
    gsettings-desktop-schemas \
    libpeas \
    libsoup-2.4 \
    gspell \
    gtksourceview4 \
    tepl \
"

inherit gnomebase gsettings itstool gnome-help gobject-introspection gtk-doc vala gettext features_check upstream-version-is-even mime-xdg python3targetconfig

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "438217bbbcf92a17c4f259b4a5426b03"
SRC_URI[archive.sha256sum] = "6887554643c5b1b3862ac364d97b7b50224bff95e6758aeaa08f4a482b554197"

GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGES += "${PN}-python"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"

FILES_${PN}-python += " \
    ${PYTHON_SITEPACKAGES_DIR} \
"

RDEPENDS_${PN} += "gsettings-desktop-schemas"
RRECOMMENDS_${PN} += "source-code-pro-fonts"
