SUMMARY = "Panel plugin displaying instant disk/partition performance"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-diskperf-plugin"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d3e627798d6a60bece47aa8b3532e1f1"

inherit xfce-panel-plugin

SRC_URI = "http://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "538119ea97a217695b297e2d165b305f"
SRC_URI[sha256sum] = "fd2e9843da5822de96a7829e50ba496c34a50fb8492d5b5f792558c6b7ce9644"
