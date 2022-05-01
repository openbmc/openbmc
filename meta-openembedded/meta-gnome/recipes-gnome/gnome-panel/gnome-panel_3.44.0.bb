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
    libgweather4 \
    gnome-menus \
    gnome-desktop \
    gdm \
"

SRC_URI[archive.sha256sum] = "99655f75d031876c130ed23c4da22c099e7bcc4048b0255a3c3a3bbc787b31aa"

PACKAGECONFIG[eds] = "--enable-eds,--disable-eds,evolution-data-server"

RDEPENDS:${PN} += "gdm-base"
