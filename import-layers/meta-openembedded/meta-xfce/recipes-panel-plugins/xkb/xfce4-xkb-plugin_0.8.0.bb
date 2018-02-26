SUMMARY = "XKB layout switching panel plug-in for the Xfce desktop environment"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-xkb-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=496f09f084b0f7e6f02f769a84490c6b"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "e4e897741ebe2827192971c2aaad835d"
SRC_URI[sha256sum] = "3b0d3b9f4b7c3e3e7be668e2f7c845b89b16e0ed3db2bacb544a17272682ced0"

DEPENDS += "libxklavier libwnck3 librsvg garcon"

FILES_${PN} += "${datadir}/xfce4/xkb"

RDEPENDS_${PN} = "xfce4-settings"
