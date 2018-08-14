DESCRIPTION = "Equake XFCE is a panel plugin for the XFCE panel which monitors earthquakes and displays an update each time a new earthquake occurs."
HOMEPAGE = "http://freecode.com/projects/equake-xfce"
SECTION = "x11/application"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit xfce-panel-plugin

DEPENDS += "curl"

SRC_URI[md5sum] = "07d42b8a3d440d6f1861048a6cc3a15a"
SRC_URI[sha256sum] = "eff9cfd604d1bd998c5208176fbe2c6da705c250dce572f5e30b2c77f4ec741c"

FILES_${PN} += "${libdir}/xfce4/panel-plugins/xfce4-equake-plugin"
