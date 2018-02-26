SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "032a2bb0a0d4a2e3c8c136910c8b551d"
SRC_URI[sha256sum] = "e8918c1255f7ab86b950ebd13555fe87046c01a1a4cb0b5d460024b6efc01769"

RRECOMMENDS_${PN} += "menulibre"
