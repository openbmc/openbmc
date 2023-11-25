SUMMARY = "Panel plugin displaying current load of the network interfaces"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-netload-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b6065ae7d3696cdad6869dd8627a9fe"

inherit xfce-panel-plugin

SRC_URI = "http://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2"
SRC_URI[sha256sum] = "9fac3a3ad52e18584bfb127cd1721d56de1004b9fdd140915fded89704ccb44e"
