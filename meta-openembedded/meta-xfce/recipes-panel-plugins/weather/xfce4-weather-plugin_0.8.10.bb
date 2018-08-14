SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-weather-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[md5sum] = "bba7f750b97c8fc3656715268edad792"
SRC_URI[sha256sum] = "ee6d43c444904631c240470e15e96215c2ce451158bfdbf234bce892bf60eab8"

FILES_${PN} += "${datadir}/xfce4/weather"
