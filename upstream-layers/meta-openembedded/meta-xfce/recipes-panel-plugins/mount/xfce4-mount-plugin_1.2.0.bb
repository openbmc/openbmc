SUMMARY = "Mount/umount utility for the xfce panel"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-mount-plugin/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI += "file://0001-check-for-fstab.h-during-configure.patch"

SRC_URI[sha256sum] = "adef71a83078e7dc45997e57411f8c43080a0204159a8b8db2ade0a9877e7b4c"
