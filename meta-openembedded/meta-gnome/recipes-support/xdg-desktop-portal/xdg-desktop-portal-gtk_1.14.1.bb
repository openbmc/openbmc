SUMMARY = "A backend implementation for xdg-desktop-portal that is using GTK and various pieces of GNOME infrastructure."
HOMEPAGE = "https://github.com/flatpak/xdg-desktop-portal-gtk"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0-native \
    gtk+3\
    xdg-desktop-portal \
    libadwaita \
    fontconfig \
    gsettings-desktop-schemas \
    gnome-desktop \
    dconf \
"

inherit gettext autotools pkgconfig gsettings features_check

REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = "git://github.com/flatpak/xdg-desktop-portal-gtk.git;protocol=https;nobranch=1"

S = "${WORKDIR}/git"
SRCREV = "952005f6a7850a247d286f14838202f506b402b7"

# gdbus-codegen wants to create files in ${B}/src and fails because of missing directory
do_configure:append() {
	mkdir -p ${B}/src
}

# Note: wlroots has its own implementation for screenshot and screencast, but
# you may want to include the according PACKAGECONFIGS for gnome.
PACKAGECONFIG ?= "wallpaper appchooser lockdown"

PACKAGECONFIG[screenshot] = "--enable-screenshot,--disable-screenshot,gnome-shell"
PACKAGECONFIG[screencast] = "--enable-screencast,--disable-screencast,mutter"
PACKAGECONFIG[wallpaper] = "--enable-wallpaper,--disable-wallpaper,gnome-desktop"
PACKAGECONFIG[background] = "--enable-background,--disable-background,gnome-shell"
PACKAGECONFIG[appchooser] = "--enable-appchooser,--disable-appchooser"
PACKAGECONFIG[lockdown] = "--enable-lockdown,--disable-lockdown"

FILES:${PN} += "${systemd_user_unitdir} ${datadir}"
