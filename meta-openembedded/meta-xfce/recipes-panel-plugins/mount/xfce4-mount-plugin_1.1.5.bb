SUMMARY = "Mount/umount utility for the xfce panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-mount-plugin"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-panel-plugin

SRC_URI += "file://0001-check-for-fstab.h-during-configure.patch"

SRC_URI[sha256sum] = "584cd954929e542b3da0ff8d69e0325d8838dc39e7b32a509d1074ce3bb58ec2"
