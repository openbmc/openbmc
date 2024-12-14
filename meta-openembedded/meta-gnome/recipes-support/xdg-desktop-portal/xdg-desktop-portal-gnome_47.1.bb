SUMMARY = "A xdg-desktop-portal backend for gnome."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/xdg-desktop-portal-gnome"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0-native \
    gtk4\
    xdg-desktop-portal \
    xdg-desktop-portal-gtk \
    libadwaita \
    fontconfig \
    gsettings-desktop-schemas \
    gnome-desktop \
    dconf \
"

RDEPENDS:${PN} = "xdg-desktop-portal xdg-desktop-portal-gtk"

inherit gnomebase pkgconfig gsettings features_check

REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI[archive.sha256sum] = "d420ff72173b04dc9491d3d7dd8cc96204f7f3f27c4cbea6024361ea9839f769"

PACKAGECONFIG ?= "screenshot screencast ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)}"
PACKAGECONFIG[wayland] = ",,wayland-native"
PACKAGECONFIG[screenshot] = ",,,gnome-shell"
PACKAGECONFIG[screencast] = ",,,mutter"

FILES:${PN} += "${systemd_user_unitdir} ${datadir}"
