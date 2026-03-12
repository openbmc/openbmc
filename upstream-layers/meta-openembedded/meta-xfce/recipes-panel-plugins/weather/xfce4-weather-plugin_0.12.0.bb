SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-weather-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCEBASEBUILDCLASS = "meson"
XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

DEPENDS += "libsoup dbus-glib upower json-c"

SRC_URI[sha256sum] = "5dd90b032c06ef4b64b818023154ef9463a2c694a0290e57f3412296c7545ff6"

FILES:${PN} += "${datadir}/xfce4/weather"
