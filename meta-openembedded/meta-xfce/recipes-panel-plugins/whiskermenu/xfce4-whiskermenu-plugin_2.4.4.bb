SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "7f6faf5ae5ca276d073798a3ff8b0e5b"
SRC_URI[sha256sum] = "624acf6d46484bb35608a76424579571423e2aefa6579f6e444f5cfb5342ff9a"

RRECOMMENDS_${PN} += "menulibre"
