SUMMARY = "Menu for quick access to folders, documents and removable media"
DESCRIPTION = "Panel plugin displaying menu with quick access to folders, documents and removable media"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-places-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b6952d9a47fc2ad0f315510e1290455f"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "f11d0e6d03f22ab02c2e6b507d365b5a918532e8819e50647ee1860eca60c743"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"
