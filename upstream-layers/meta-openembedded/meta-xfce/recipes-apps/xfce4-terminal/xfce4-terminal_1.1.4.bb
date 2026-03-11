SUMMARY = "Terminal emulator for the Xfce desktop environment"
HOMEPAGE = "https://docs.xfce.org/apps/xfce4-terminal/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui gtk-doc-native"

inherit xfce-app

FILES:${PN} += " \
    ${datadir}/xfce4 \
    ${datadir}/gnome-control-center \
"

SRC_URI[sha256sum] = "873c921da1f4b986ffb459d4960789c9c063af98648c9f0ca146dc6f6f5b71b7"

RRECOMMENDS:${PN} += "vte-prompt"
