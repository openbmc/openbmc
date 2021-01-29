SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[sha256sum] = "a6f7070afd1c9020257d8ed7483872643a6cddd76d5d73de107c7fbd981fc515"

RRECOMMENDS_${PN} += "menulibre"
