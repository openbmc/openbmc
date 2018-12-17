SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[md5sum] = "d2383c3eb1b54adf5a206973bd20f159"
SRC_URI[sha256sum] = "7e569bb0dff7e3db3d964e23323f54c6de7249741c6a0e33a0fa501d83040b16"

RRECOMMENDS_${PN} += "menulibre"
