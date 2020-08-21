SUMMARY = "Panel plugin with graphical representation of the cpu frequency"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-eyes-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "4db780178e529391d53da180e49386904e69a5a33b3bd5185835d0a7e6ff5ac5"

FILES_${PN} += "${datadir}/xfce4/eyes"
