SUMMARY = "A simple text editor for Xfce"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gtk+3 gtksourceview4 xfconf xfce4-dev-tools-native"

inherit xfce-app gsettings mime-xdg

SRC_URI[sha256sum] = "921ebbcfdfd5e2e56f31a5177a2d26f46c758cc972595017bca9e0a6a3c3a721"

PACKAGECONFIG ??= ""
PACKAGECONFIG[spell] = "--enable-plugin-gspell,--disable-plugin-gspell,gspell"

FILES:${PN} += " \
    ${datadir}/glib-2.0/schemas \
    ${datadir}/metainfo \
    ${datadir}/polkit-1 \
"
