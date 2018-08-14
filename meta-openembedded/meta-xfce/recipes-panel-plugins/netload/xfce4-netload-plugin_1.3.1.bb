SUMMARY = "Panel plugin displaying current load of the network interfaces"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-netload-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35a7203c41b86d15546dddc05995f97f"

inherit xfce-panel-plugin

SRC_URI = "http://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2 \
           file://0001-Do-not-include-sys-sysctl.h-its-unused-on-linux-port.patch \
           "
SRC_URI[md5sum] = "f25aa3242e3119b49f259a3e4a1af08b"
SRC_URI[sha256sum] = "99762781099d1e0ab9aa6a7b30c2bd94d8f658dbe61c760410d5d42d0766391c"
