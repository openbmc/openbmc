SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui"

inherit xfce-app

FILES_${PN} += "${datadir}/xfce4 \
                ${datadir}/gnome-control-center"

SRC_URI[md5sum] = "cb995e4891a3c547bf133b31e4840d01"
SRC_URI[sha256sum] = "0deb0d06e50a8a41fb00e2c3773f0793882cb9f073ae16ead887bb9681c514cd"

RRECOMMENDS_${PN} += "vte-prompt"
