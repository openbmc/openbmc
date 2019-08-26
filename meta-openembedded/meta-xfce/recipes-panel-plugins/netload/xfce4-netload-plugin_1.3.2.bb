SUMMARY = "Panel plugin displaying current load of the network interfaces"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-netload-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35a7203c41b86d15546dddc05995f97f"

inherit xfce-panel-plugin

SRC_URI = "http://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "ae4652711812a77a58c3dc96650a74dc"
SRC_URI[sha256sum] = "22e40425cfe1e07b01fe275b1afddc7c788af34d9c2c7e2842166963cb41215d"
