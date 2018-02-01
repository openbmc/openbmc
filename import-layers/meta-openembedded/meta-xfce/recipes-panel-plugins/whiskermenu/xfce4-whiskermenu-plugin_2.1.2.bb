SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "eb07c2107c9d1276e9b1bec01f7347a1"
SRC_URI[sha256sum] = "efd95c330055fd7901a59a48569d14885c168017c5fdb2e233976a78bccb8923"

RRECOMMENDS_${PN} += "menulibre"
