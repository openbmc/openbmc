SUMMARY = "Notes plugin for the Xfce Panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-notes-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-panel-plugin

DEPENDS += "gtk+3 libxfce4ui xfce4-panel xfconf"

SRC_URI[sha256sum] = "13f909c948b639f96de64cf793eb74cb1779589201d3933eff214ee8f35ab088"
