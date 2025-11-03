SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-weather-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[sha256sum] = "a45146f9a0dcdc95d191c09c64ad279ae289cf8f811c4433e08e31a656845239"

FILES:${PN} += "${datadir}/xfce4/weather"
