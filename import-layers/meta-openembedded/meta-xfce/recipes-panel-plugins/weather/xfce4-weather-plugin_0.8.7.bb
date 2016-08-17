SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-weather-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[md5sum] = "ecab0eaad870e460da4597e76f43e6e6"
SRC_URI[sha256sum] = "071e71106868c7d90c936256d837ca834d0ca6f54daea59a9b5fc11b318e65b0"

FILES_${PN} += "${datadir}/xfce4/weather"
