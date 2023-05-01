SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui gtk-doc-native"

inherit xfce-app

FILES:${PN} += " \
    ${datadir}/xfce4 \
    ${datadir}/gnome-control-center \
"

SRC_URI[sha256sum] = "78e55957af7c6fc1f283e90be33988661593a4da98383da1b0b54fdf6554baf4"

RRECOMMENDS:${PN} += "vte-prompt"
