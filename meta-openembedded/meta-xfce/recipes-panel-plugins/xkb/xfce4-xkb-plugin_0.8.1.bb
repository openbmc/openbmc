SUMMARY = "XKB layout switching panel plug-in for the Xfce desktop environment"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-xkb-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=496f09f084b0f7e6f02f769a84490c6b"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "72530bf59d7cd902326469e5a7a9892b"
SRC_URI[sha256sum] = "c19ecf126201deb6148741c521124771ad396adc874471512ab5ffe1946567a1"

DEPENDS += "libxklavier libwnck3 librsvg garcon"

FILES_${PN} += "${datadir}/xfce4/xkb"

RDEPENDS_${PN} = "xfce4-settings"
