SUMMARY = "Menu for quick access to folders, documents and removable media"
DESCRIPTION = "Panel plugin displaying menu with quick access to folders, documents and removable media"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-places-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b6952d9a47fc2ad0f315510e1290455f"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "bde92cbd08f129d517524784e5060816"
SRC_URI[sha256sum] = "f211219f03c9260f624370e18c79e4176c9d35a8247158e77e5d811327610ab2"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"
