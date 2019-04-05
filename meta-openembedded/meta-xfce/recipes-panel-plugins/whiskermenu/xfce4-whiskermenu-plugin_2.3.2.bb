SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "e9aa6c33463bd564c47e1943c0fc7ac3"
SRC_URI[sha256sum] = "f1bf6be37f7778a486b16ef115d4c05120cc12c87e4af3af9c5001276bdcb7cc"

RRECOMMENDS_${PN} += "menulibre"
