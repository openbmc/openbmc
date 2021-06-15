SUMMARY = "XKB layout switching panel plug-in for the Xfce desktop environment"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-xkb-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=496f09f084b0f7e6f02f769a84490c6b"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "bb4be13f6f73cd86a6d939e1a6b125841cf266415bc4fd134a511e4f0cf97967"

DEPENDS += "libxklavier libwnck3 librsvg garcon"

FILES_${PN} += "${datadir}/xfce4/xkb"

RDEPENDS_${PN} = "xfce4-settings"
