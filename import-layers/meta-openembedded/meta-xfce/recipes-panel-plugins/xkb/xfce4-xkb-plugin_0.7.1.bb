SUMMARY = "XKB layout switching panel plug-in for the Xfce desktop environment"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-xkb-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=496f09f084b0f7e6f02f769a84490c6b"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "2f68e0d53baf68ecc1a7165ad33c26a9"
SRC_URI[sha256sum] = "61fe2e33fe99939d5d06a682e94e40e05ef844c930ad612154090d158b2ce681"

DEPENDS += "libxklavier libwnck librsvg garcon"

FILES_${PN} += "${datadir}/xfce4/xkb"

RDEPENDS_${PN} = "xfce4-settings"
