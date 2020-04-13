SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-weather-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[md5sum] = "203ad16e74d4823f6fb6e9a18bb7df55"
SRC_URI[sha256sum] = "afb2af5f3effc2ea6181636ed0e82e6dafd556ec1b8478100802f85a5d167a89"

FILES_${PN} += "${datadir}/xfce4/weather"
