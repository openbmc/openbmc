SUMMARY = "Panel plugin displaying instant disk/partition performance"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-diskperf-plugin/start"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d3e627798d6a60bece47aa8b3532e1f1"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI = "https://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.${XFCE_COMPRESS_TYPE}"
SRC_URI[sha256sum] = "3833920a3a4a81b3c676c4fab6dd178f4a222d66f316a0783a9149a0153b7fb6"
