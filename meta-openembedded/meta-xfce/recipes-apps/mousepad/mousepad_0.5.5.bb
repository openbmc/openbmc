SUMMARY = "A simple text editor for Xfce"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gtk+3 gtksourceview4 xfconf xfce4-dev-tools-native"

inherit xfce-app gsettings mime-xdg

SRC_URI += "file://0001-Plugin-support-Properly-handle-plugin-settings.patch"

SRC_URI[sha256sum] = "40c35f00e0e10df50a59bd0dbba9007d2fb5574ed8a2aa73b0fc5ef40e64abe1"

PACKAGECONFIG ??= ""
PACKAGECONFIG[spell] = "--enable-plugin-gspell,--disable-plugin-gspell,gspell"

FILES:${PN} += " \
    ${datadir}/glib-2.0/schemas \
    ${datadir}/metainfo \
    ${datadir}/polkit-1 \
"
