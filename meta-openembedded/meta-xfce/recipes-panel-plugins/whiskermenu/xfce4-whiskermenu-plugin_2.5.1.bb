SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[sha256sum] = "b2074f8a9caa766e2d97f0b960ef2f4d1e20dd804497229bc1f0157791896925"

RRECOMMENDS_${PN} += "menulibre"
