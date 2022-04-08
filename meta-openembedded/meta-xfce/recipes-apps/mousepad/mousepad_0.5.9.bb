SUMMARY = "A simple text editor for Xfce"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gtk+3 gtksourceview4 xfconf xfce4-dev-tools-native"

inherit xfce-app gsettings mime-xdg

SRC_URI[sha256sum] = "f108a8c167ec5727266ab67666f10dbd60e972d56ea03944302fdabb2167f473"

PACKAGECONFIG ??= ""
PACKAGECONFIG[spell] = "--enable-plugin-gspell,--disable-plugin-gspell,gspell"

FILES:${PN} += " \
    ${datadir}/glib-2.0/schemas \
    ${datadir}/metainfo \
    ${datadir}/polkit-1 \
"
