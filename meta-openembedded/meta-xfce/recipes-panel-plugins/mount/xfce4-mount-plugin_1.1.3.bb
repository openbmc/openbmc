SUMMARY = "Mount/umount utility for the xfce panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-mount-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-panel-plugin

SRC_URI += "file://0001-check-for-fstab.h-during-configure.patch"
SRC_URI[md5sum] = "2f1f903d0bdf6ee6776afd8af73497ac"
SRC_URI[sha256sum] = "aae5bd6b984bc78daf6b5fb9d15817a27253674a4264ad60f62ccb1aa194911e"
