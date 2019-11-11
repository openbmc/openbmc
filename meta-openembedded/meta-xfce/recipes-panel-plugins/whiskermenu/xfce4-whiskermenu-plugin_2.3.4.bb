SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "ee9e378fae78b230a23b241cf727c84b"
SRC_URI[sha256sum] = "84d3de35695a023aab181f7a9b75f59029eb3b07c3e47a5e11e8bd79db62570e"

RRECOMMENDS_${PN} += "menulibre"
