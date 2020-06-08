SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui"

inherit xfce-app

FILES_${PN} += "${datadir}/xfce4 \
                ${datadir}/gnome-control-center"

SRC_URI[md5sum] = "d0308313def5d7cc51070a6db1cf24dc"
SRC_URI[sha256sum] = "9ba23bf86d350ef8a95d2dfb50bbd1bbb2144d82985a779ec28caf47faaeeeeb"

RRECOMMENDS_${PN} += "vte-prompt"
