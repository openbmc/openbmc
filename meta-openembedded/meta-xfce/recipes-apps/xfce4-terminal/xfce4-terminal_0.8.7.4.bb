SUMMARY = "Terminal emulator for the Xfce desktop environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui"

inherit xfce-app

FILES_${PN} += "${datadir}/xfce4 \
                ${datadir}/gnome-control-center"

SRC_URI[md5sum] = "c861540dd1dd05f56e62382b3851cf66"
SRC_URI[sha256sum] = "a88f98af4da72394f2cfbd7f14b0f053ec0a3b58a4f6a577836357c60a6c42ab"
