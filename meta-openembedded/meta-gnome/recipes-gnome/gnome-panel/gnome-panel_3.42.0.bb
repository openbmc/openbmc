SUMMARY = "GNOME flashback panel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit gnomebase gsettings itstool gnome-help gtk-icon-cache gtk-doc gettext upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam"

DEPENDS += " \
    yelp-tools-native \
    libwnck3 \
    polkit \
    dconf \
    libgweather \
    gnome-menus \
    gnome-desktop \
    gdm \
"

SRC_URI[archive.sha256sum] = "aea8c0efc2edba20e97ad4695179cd0a2538e64c2904702cc8c594e8e0898703"

PACKAGECONFIG[eds] = "--enable-eds,--disable-eds,evolution-data-server"

RDEPENDS:${PN} += "gdm-base"
