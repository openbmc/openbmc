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

SRC_URI[archive.md5sum] = "cfd5d3fd548a2afdd8bd3bbdf9646bbd"
SRC_URI[archive.sha256sum] = "a6bc0255252eeb4b964bcbe55fd7908b69f914c062c5ec8dff5ac0262d29b90d"
SRC_URI += " \
    file://0001-Do-not-try-to-find-ZONEINFO-it-tries-to-run-compiled.patch \
"

PACKAGECONFIG[eds] = "--enable-eds,--disable-eds,evolution-data-server"

RDEPENDS_${PN} += "gdm-base"
