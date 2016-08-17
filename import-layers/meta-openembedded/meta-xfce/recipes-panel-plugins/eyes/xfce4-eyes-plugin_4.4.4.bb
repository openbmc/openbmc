SUMMARY = "Panel plugin with graphical representation of the cpu frequency"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-eyes-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "136f52a256fad8cfd29fb2976e08ebc8"
SRC_URI[sha256sum] = "240ce85b68d3d161f276ebbea97072dd6ee3df77062fd073bf6eeb4d3d1400ca"

FILES_${PN} += "${datadir}/xfce4/eyes"
