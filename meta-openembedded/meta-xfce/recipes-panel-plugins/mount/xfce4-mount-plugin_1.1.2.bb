SUMMARY = "Mount/umount utility for the xfce panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-mount-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-panel-plugin

SRC_URI += "file://0001-check-for-fstab.h-during-configure.patch"
SRC_URI[md5sum] = "7eba9696d82433a5577741214d34b588"
SRC_URI[sha256sum] = "54578447abaf9da630a750d64acdc37d4fd20dda6460208d6d1ffaa9e43ee1a6"
