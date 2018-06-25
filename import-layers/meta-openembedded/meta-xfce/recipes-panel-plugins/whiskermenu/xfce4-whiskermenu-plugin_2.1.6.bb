SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "b2b2bacc8d5f3c3119a0f317b6074276"
SRC_URI[sha256sum] = "8b2a8ee1445df39a2cda139e353f2e9ec3720a780296dc41b7d4aebde57371fc"

RRECOMMENDS_${PN} += "menulibre"
