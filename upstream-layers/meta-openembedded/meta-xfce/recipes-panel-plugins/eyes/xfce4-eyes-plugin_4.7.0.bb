SUMMARY = "Panel plugin with graphical representation of the cpu frequency"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-eyes-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "87f9b978ca75abb3aa5edb1315eb65ef98654a662c14621847ddffe8aa6574ad"

FILES:${PN} += "${datadir}/xfce4/eyes"
