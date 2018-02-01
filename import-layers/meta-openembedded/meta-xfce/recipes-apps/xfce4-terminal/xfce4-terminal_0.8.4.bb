SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui"

inherit xfce-app

FILES_${PN} += "${datadir}/xfce4 \
                ${datadir}/gnome-control-center"

SRC_URI[md5sum] = "7d9ea57301d6a770e5db8e7ecd6e4cda"
SRC_URI[sha256sum] = "c5c1163b30e7a43d56ff92a25193bf9f29ce60e6cf43e5988530df79c84cfdc8"
