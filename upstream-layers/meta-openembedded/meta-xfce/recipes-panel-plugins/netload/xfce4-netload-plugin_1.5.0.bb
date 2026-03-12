SUMMARY = "Panel plugin displaying current load of the network interfaces"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-netload-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b6065ae7d3696cdad6869dd8627a9fe"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI = "https://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.${XFCE_COMPRESS_TYPE}"
SRC_URI[sha256sum] = "a868be8f73e8396c2d61903d46646993c5130d16ded71ddb5da9088abf7cb7ba"
