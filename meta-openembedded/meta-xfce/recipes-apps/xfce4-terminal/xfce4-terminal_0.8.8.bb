SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui"

inherit xfce-app

FILES_${PN} += "${datadir}/xfce4 \
                ${datadir}/gnome-control-center"

SRC_URI[md5sum] = "4295d4d783f6d6dfe92f5bb15d96f6c6"
SRC_URI[sha256sum] = "8fba6a60d3a0fee07417ad7c36bf78cc45be1b27f0759e125051aa73f08487fd"

RRECOMMENDS_${PN} += "vte-prompt"
