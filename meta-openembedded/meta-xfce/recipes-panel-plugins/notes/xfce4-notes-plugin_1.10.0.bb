SUMMARY = "Notes plugin for the Xfce Panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-notes-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "gtk+3 libxfce4ui xfce4-panel xfconf"

SRC_URI[sha256sum] = "2ee4406042edd352a91e166c83b60d13220ef04dce3fa6b9e0eb13636d636929"
