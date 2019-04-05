SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-weather-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[md5sum] = "e0bde3ba6a7e863022a86a23c7871255"
SRC_URI[sha256sum] = "7cdc18b8df759dee4ceaaf6ce303eff7fda48e247dbc26b78142929213506cfd"

FILES_${PN} += "${datadir}/xfce4/weather"
