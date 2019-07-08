DESCRIPTION = "Panel plugin displaying menu with quick access to folders, documents, and removable media"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-places-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b6952d9a47fc2ad0f315510e1290455f"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "fcae9b38a8affcd82699a94991bba29b"
SRC_URI[sha256sum] = "4175c614749abbb5bcf6f49c88125fb0dd36db69f4c374df23563907b16e2c3f"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"
