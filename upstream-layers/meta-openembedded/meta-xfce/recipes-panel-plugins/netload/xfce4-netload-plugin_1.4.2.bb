SUMMARY = "Panel plugin displaying current load of the network interfaces"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-netload-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b6065ae7d3696cdad6869dd8627a9fe"

inherit xfce-panel-plugin

SRC_URI = "http://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2"
SRC_URI[sha256sum] = "a2041338408b2670f8debe57fcec6af539f704659eba853943c1524936ebabeb"
