SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "adb064538b2e2cbc7ddd1d8ac57cec36"
SRC_URI[sha256sum] = "f5241910ea6411840b8c9f9471f0d262ab0583150bb82f9b280eccbaadb0ebbe"

RRECOMMENDS_${PN} += "menulibre"
