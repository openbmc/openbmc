SUMMARY = "Xfce Calender"
HOMEPAGE = "https://docs.xfce.org/apps/orage/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gtk+ xfce4-panel libical popt"

inherit xfce-app mime-xdg

SRC_URI[sha256sum] = "6bfd3da084c2977fb5cee26c8e94bf55e358da8e86dd2a83c6fa9174f24672a1"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-libnotify,--disable-libnotify,libnotify"

PACKAGES =+ "xfce4-orageclock-plugin"
FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/themes \
"
FILES:xfce4-orageclock-plugin = "${libdir}/xfce4/panel/plugins/*.so ${datadir}/xfce4/panel/plugins"
FILES:${PN}-dev += "${libdir}/xfce4/panel/plugins/*.la"
