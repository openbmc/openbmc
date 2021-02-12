SUMMARY = "A simple text editor for Xfce"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gtk+3 gtksourceview3 xfconf xfce4-dev-tools-native"

inherit xfce-app gsettings mime-xdg

SRC_URI[sha256sum] = "30adc5631a4e551df7b3e17ddc1d10e9980080f96518dedccc32170cb18efb8e"

FILES_${PN} += " \
    ${datadir}/glib-2.0/schemas \
    ${datadir}/metainfo \
    ${datadir}/polkit-1 \
"
