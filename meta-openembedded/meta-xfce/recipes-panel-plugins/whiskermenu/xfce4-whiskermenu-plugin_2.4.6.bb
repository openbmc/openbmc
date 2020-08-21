SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "81a4a4c7635273485fac5c2d98e48d02"
SRC_URI[sha256sum] = "8974d38cc87df528693efe4b6e14bcd233cdb49d2018a23ddddf745110b25744"

RRECOMMENDS_${PN} += "menulibre"
