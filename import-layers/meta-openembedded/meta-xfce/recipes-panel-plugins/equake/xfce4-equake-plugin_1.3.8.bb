DESCRIPTION = "Equake XFCE is a panel plugin for the XFCE panel which monitors earthquakes and displays an update each time a new earthquake occurs."
HOMEPAGE = "http://freecode.com/projects/equake-xfce"
SECTION = "x11/application"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "c8f6cb2aec62513c343281eacc4fc395"
SRC_URI[sha256sum] = "431575da3d49fea3afa60f02e83c8d74e20e9a229c9c4ec82c21d45e2c986925"

FILES_${PN} += "${libdir}/xfce4/panel-plugins/xfce4-equake-plugin"
