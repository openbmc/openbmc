SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-weather-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[md5sum] = "0c56c057e1c354b30409b7871ab6f314"
SRC_URI[sha256sum] = "0e15d14b3e18c3da46ad23ee3158a25220f1474a48b611de96edb56221aecee5"

FILES_${PN} += "${datadir}/xfce4/weather"
