SUMMARY = "Panel plugin displaying current load of the network interfaces"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-netload-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=35a7203c41b86d15546dddc05995f97f"

inherit xfce-panel-plugin

SRC_URI = "http://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2"
SRC_URI[sha256sum] = "6c76260e101790754dd93255ec979accd97d21a21da85d8edcd6c7b01ddcd70c"
