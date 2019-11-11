SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-weather-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup-2.4 dbus-glib upower"

SRC_URI[md5sum] = "7ff4ab636f93addba0817bf6436d2964"
SRC_URI[sha256sum] = "4423a0c27830fd1f08e063aaefbf70f31d89235d75549ca841d677ab2e409572"

FILES_${PN} += "${datadir}/xfce4/weather"
