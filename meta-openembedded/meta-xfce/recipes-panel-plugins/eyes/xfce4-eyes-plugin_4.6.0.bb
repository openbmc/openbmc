SUMMARY = "Panel plugin with graphical representation of the cpu frequency"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-eyes-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "ad0ff05d88ba393b7c8922f8233edd33fc0a4e8b000b61de1f8f3a10c5ae5324"

FILES:${PN} += "${datadir}/xfce4/eyes"
