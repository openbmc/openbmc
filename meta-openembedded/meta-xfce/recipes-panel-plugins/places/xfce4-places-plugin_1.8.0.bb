SUMMARY = "Menu for quick access to folders, documents and removable media"
DESCRIPTION = "Panel plugin displaying menu with quick access to folders, documents and removable media"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-places-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b6952d9a47fc2ad0f315510e1290455f"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "e3a306d927befb8afcb5212f8ab0b8c9"
SRC_URI[sha256sum] = "7ba3f46f88c2845cbf413efeefaed29157f8b98571856c6e2bf35e4de5d8ecce"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"
