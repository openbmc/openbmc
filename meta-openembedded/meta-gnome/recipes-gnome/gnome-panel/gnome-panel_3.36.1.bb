SUMMARY = "GNOME flashback panel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit gnomebase gsettings itstool gnome-help gtk-icon-cache gtk-doc gettext upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam"

DEPENDS += " \
    yelp-tools-native \
    libwnck3 \
    polkit \
    dconf \
    libgweather \
    gnome-menus3 \
    gnome-desktop3 \
    gdm \
"

SRC_URI[archive.md5sum] = "b6bb185ce1724f4b19699042d90282a6"
SRC_URI[archive.sha256sum] = "1e21b726dd397523ae79d29eb538bcae09e3427e23ccd63f28eb25ef3552abd3"

PACKAGECONFIG[eds] = "--enable-eds,--disable-eds,evolution-data-server"

RDEPENDS_${PN} += "gdm-base"
