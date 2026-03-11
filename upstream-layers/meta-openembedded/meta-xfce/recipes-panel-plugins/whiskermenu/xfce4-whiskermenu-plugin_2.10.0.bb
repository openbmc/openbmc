SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce-panel-plugin cmake

SRC_URI[sha256sum] = "c2efb3782816d44d421dcbee2900b9513bdb2469b695b776641f495601f33a10"
