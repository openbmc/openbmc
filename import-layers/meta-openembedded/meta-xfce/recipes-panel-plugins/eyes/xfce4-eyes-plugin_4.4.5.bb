SUMMARY = "Panel plugin with graphical representation of the cpu frequency"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-eyes-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "b2881924fdcc1230d9fa82c10f1a6afe"
SRC_URI[sha256sum] = "648f7a1738d852af9482d11330b8ab33901f05256984de73d8cea4d7d7311937"

FILES_${PN} += "${datadir}/xfce4/eyes"
