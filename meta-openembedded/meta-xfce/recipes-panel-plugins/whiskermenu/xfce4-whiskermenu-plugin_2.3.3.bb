SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "21f1e1d8d4a030f23d358d6c5ac50f2a"
SRC_URI[sha256sum] = "e0c6cc4fc1e685bc9601ec217a803d5489ca3276f4008bf056600471c6ac1848"

RRECOMMENDS_${PN} += "menulibre"
