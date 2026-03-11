SUMMARY = "GNOME flashback panel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gsettings itstool gnome-help gtk-icon-cache gettext upstream-version-is-even features_check

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

SRC_URI[archive.sha256sum] = "d8f6e2c659668c77df82c3dd95ba04fcef8c40c228e2c22605f9a17a9161ec83"

PACKAGECONFIG[eds] = "--enable-eds,--disable-eds,evolution-data-server"

RDEPENDS:${PN} += "gdm-base"
