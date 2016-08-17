SUMMARY = "Xfce Calender"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"
DEPENDS = "gtk+ xfce4-panel libical popt"

inherit xfce-app

SRC_URI[md5sum] = "2b7f5d38cb5c6edbcc65d0f52a742e46"
SRC_URI[sha256sum] = "3cf9aa441ae83c8688865f82217025cdf3ebaa152cce4571777b8c2aa8dd9062"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-libnotify,--disable-libnotify,libnotify"

PACKAGES =+ "xfce4-orageclock-plugin"
FILES_${PN} += "${datadir}/dbus-1"
FILES_${PN}-dbg += "${libdir}/xfce4/panel/plugins/.debug"
FILES_xfce4-orageclock-plugin = "${libdir}/xfce4/panel/plugins/*.so ${datadir}/xfce4/panel/plugins"

