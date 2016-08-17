SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+ vte libxfce4ui"

inherit xfce-app

FILES_${PN} += "${datadir}/xfce4 \
                ${datadir}/gnome-control-center"

SRC_URI[md5sum] = "6a2816d8b0933cd707ed456ceb731399"
SRC_URI[sha256sum] = "912f4716c2395a14a80620ef982b4af1e2a67a8df9a1ef0b802ecae826057e08"
