SUMMARY = "Ptyxis is a terminal for GNOME with first-class support for containers"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/ptyxis"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GTKIC_VERSION = "4"
inherit gnomebase gsettings pkgconfig gtk-icon-cache gettext
REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = " \
    desktop-file-utils-native \
    gtk4-native \
    glib-2.0 \
    gsettings-desktop-schemas \
    gtk4 \
    json-glib \
    libadwaita \
    libportal \
    vte \
"

SRC_URI[archive.sha256sum] = "73f4b76480644b2840415859a51d20cd7487b0619714f8defcd38212c3ddcffe"

FILES:${PN} += "${datadir}"

RDEPENDS:${PN} += "hicolor-icon-theme"
