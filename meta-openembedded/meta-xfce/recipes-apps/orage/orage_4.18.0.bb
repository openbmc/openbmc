SUMMARY = "Xfce Calender"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gtk+ xfce4-panel libical popt"

inherit xfce-app mime-xdg

SRC_URI[sha256sum] = "6313b49b26cfa39fc5e99468f3fbcfe0fa1c0f3f74273f03674f1a7d6141a3ec"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-libnotify,--disable-libnotify,libnotify"

PACKAGES =+ "xfce4-orageclock-plugin"
FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
FILES:xfce4-orageclock-plugin = "${libdir}/xfce4/panel/plugins/*.so ${datadir}/xfce4/panel/plugins"
FILES:${PN}-dev += "${libdir}/xfce4/panel/plugins/*.la"
