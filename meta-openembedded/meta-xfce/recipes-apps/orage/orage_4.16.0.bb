SUMMARY = "Xfce Calender"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gtk+ xfce4-panel libical popt"

inherit xfce-app mime-xdg

SRC_URI[sha256sum] = "26111a3b6a2007c82f1e0a1e0591b774a0b132f3a7f1cde53d9be661b2f11700"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-libnotify,--disable-libnotify,libnotify"

PACKAGES =+ "xfce4-orageclock-plugin"
FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
FILES:xfce4-orageclock-plugin = "${libdir}/xfce4/panel/plugins/*.so ${datadir}/xfce4/panel/plugins"
FILES:${PN}-dev += "${libdir}/xfce4/panel/plugins/*.la"
