SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-weather-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[md5sum] = "29fe8892bb4c6b1e639862a63110618e"
SRC_URI[sha256sum] = "651b722714fdafde3f548f183958c34e1539cf563193d7e51f28bfb6933d6d3f"

FILES_${PN} += "${datadir}/xfce4/weather"
