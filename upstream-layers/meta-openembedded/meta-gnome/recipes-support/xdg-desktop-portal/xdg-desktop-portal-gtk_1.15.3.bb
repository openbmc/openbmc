SUMMARY = "A backend implementation for xdg-desktop-portal that is using GTK and various pieces of GNOME infrastructure."
HOMEPAGE = "https://github.com/flatpak/xdg-desktop-portal-gtk"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0-native \
    gtk+3\
    xdg-desktop-portal \
    libadwaita \
    dconf \
"

inherit gettext meson pkgconfig gsettings features_check

REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = "git://github.com/flatpak/xdg-desktop-portal-gtk.git;protocol=https;branch=main"

SRCREV = "337202d4e7179857bc37b03c1a6d8c9d92e47c44"

PACKAGECONFIG ?= "wallpaper appchooser lockdown settings"

PACKAGECONFIG[wallpaper] = "-Dwallpaper=enabled,-Dwallpaper=disabled,gnome-desktop"
PACKAGECONFIG[settings] = "-Dsettings=enabled,-Dsettings=disabled,gsettings-desktop-schemas fontconfig"
PACKAGECONFIG[appchooser] = "-Dappchooser=enabled,-Dappchooser=disabled"
PACKAGECONFIG[lockdown] = "-Dlockdown=enabled,-Dlockdown=disabled"

FILES:${PN} += "${systemd_user_unitdir} ${datadir}"
