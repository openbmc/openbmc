SUMMARY = "XKB layout switching panel plug-in for the Xfce desktop environment"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-xkb-plugin/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "7cd7f3626ef39dc4ce142b2f96ab7583cbea84b4c0352fbc9c9667faac0bdd12"

DEPENDS += "libxklavier libwnck3 librsvg garcon"

FILES:${PN} += "${datadir}/xfce4/xkb"

RDEPENDS:${PN} = "xfce4-settings"
