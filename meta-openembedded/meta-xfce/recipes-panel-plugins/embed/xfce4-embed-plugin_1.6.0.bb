DESCRIPTION = "A panel plugin that embeds arbitrary windows (or GtkPlug widgets) into the panel."
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-embed-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

# DEPENDS += "xfconf xorgproto libxtst"

SRC_URI[md5sum] = "6870b116b85e4fa68d1b9ef76bd1d279"
SRC_URI[sha256sum] = "c767df6360e8194b32bc24823dd000975edba0cafe525c23d7854029359ee228"
