SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui gtk-doc-native"

inherit xfce-app

FILES:${PN} += " \
    ${datadir}/xfce4 \
    ${datadir}/gnome-control-center \
"

SRC_URI[sha256sum] = "593b6a7bd9b18851e51854e075990109b7896a22713b5dd8b913b23f21db6576"

RRECOMMENDS:${PN} += "vte-prompt"
