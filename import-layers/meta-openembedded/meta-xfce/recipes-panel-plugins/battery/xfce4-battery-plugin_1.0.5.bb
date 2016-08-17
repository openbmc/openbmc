SUMMARY = "A battery monitor panel plugin for Xfce4, compatible with APM and ACP"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-battery-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-panel-plugin

SRC_URI += "file://fix-build-on-aarch64.patch"

SRC_URI[md5sum] = "ca2d394e411a20442a519efa0d14f8ec"
SRC_URI[sha256sum] = "f659b1af40ab72c93448affaa693ab551827a5600ce9b97a799b7c2419bdeb11"
