SUMMARY = "Panel plugin to display current temperature and weather condition"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-weather-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "libsoup dbus-glib upower json-c"

SRC_URI += "file://0001-libxfce4ui-Avoid-deprecated-functions.patch \
            file://0002-parsers-Generalise-input-to-array-of-gchar.patch \
            file://0003-libsoup-Port-to-libsoup-3.0.patch \
            file://0004-Report-UPower-Glib-support.patch \
            file://0005-Make-libsoup-v3-support-optional.patch \
"
SRC_URI[sha256sum] = "002d1fe63906d2f3a012f3cb58cceff1dfbcc466759e36c76d3b03dd01c0dc57"

FILES:${PN} += "${datadir}/xfce4/weather"
