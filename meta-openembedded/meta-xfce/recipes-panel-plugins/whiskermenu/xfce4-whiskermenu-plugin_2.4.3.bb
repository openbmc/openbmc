SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "7b66438996127b759ad634f6579e003c"
SRC_URI[sha256sum] = "39faeee91ceb3cb727f9de09dbf20a8c73e524851a2c3b76a4b19a0732de5ff0"

RRECOMMENDS_${PN} += "menulibre"
