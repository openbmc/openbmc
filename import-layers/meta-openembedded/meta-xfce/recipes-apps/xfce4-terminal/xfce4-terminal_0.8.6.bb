SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui"

inherit xfce-app

FILES_${PN} += "${datadir}/xfce4 \
                ${datadir}/gnome-control-center"

SRC_URI[md5sum] = "92f5a3366e30f5f8238d8250f730b6af"
SRC_URI[sha256sum] = "bc2a560409a0f0b666d1c557e991748b986ec27572a45ae88b0ee5a480d881d7"
