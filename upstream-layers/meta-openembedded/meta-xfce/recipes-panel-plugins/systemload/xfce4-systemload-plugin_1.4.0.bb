DESCRIPTION = "Panel plugin displaying current CPU load, the memory in use, the swap space and the system uptime"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-systemload-plugin/start"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9acb172a93ff6c43cce2aff790a8aef8"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

PACKAGECONFIG ?= "network power"
PACKAGECONFIG[power] = ",,upower"
PACKAGECONFIG[network] = ",,libgtop"

SRC_URI += "file://convert-gulong.patch"
SRC_URI[sha256sum] = "6e363bcf845bb88329b52858d65a1ec6e00db5121ae9246e46eb03135d9569c6"
