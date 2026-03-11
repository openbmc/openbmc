SUMMARY = "Panel plugin with graphical representation of the cpu frequency"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-eyes-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "5219b2ec0f203ab65990671a95b1607f539201e09e8910b854aea848d478cb53"

FILES:${PN} += "${datadir}/xfce4/eyes"
