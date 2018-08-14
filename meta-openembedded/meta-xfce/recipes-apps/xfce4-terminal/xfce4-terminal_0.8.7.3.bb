SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui"

inherit xfce-app

FILES_${PN} += "${datadir}/xfce4 \
                ${datadir}/gnome-control-center"

SRC_URI[md5sum] = "f014d613438848b452f1f9eecbf7027d"
SRC_URI[sha256sum] = "dafa155177934fc7dce2d4efb33402f74b1a593a17ff17d2187a6429f599dfd6"
