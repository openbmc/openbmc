SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-weather-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[md5sum] = "25d3d9fb2e688a619201655f6eea51c2"
SRC_URI[sha256sum] = "34368cf2332774ad2a05226b2914ecb60e7550e9b2164be53ebe8f370198bb3d"

FILES_${PN} += "${datadir}/xfce4/weather"
