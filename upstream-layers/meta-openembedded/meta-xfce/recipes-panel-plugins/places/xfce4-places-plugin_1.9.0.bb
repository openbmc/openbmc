SUMMARY = "Menu for quick access to folders, documents and removable media"
DESCRIPTION = "Panel plugin displaying menu with quick access to folders, documents and removable media"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-places-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b6952d9a47fc2ad0f315510e1290455f"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "76d95687e0bea267465e832eea6266563a18d2219192f9e8af6f88e899262e43"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"
