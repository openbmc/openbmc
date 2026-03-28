SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce-panel-plugin cmake

SRC_URI[sha256sum] = "8b5a777a5d9df1f1c39b109ba8b2d045cb2cc02c41a9a297aa00dcb2a4520530"
