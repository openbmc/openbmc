DESCRIPTION = "Parole is a modern simple media player based on the GStreamer framework"
HOMEPAGE = "https://docs.xfce.org/apps/parole/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit xfce-app gtk-doc mime mime-xdg

DEPENDS += " \
    dbus-glib \
    xfce4-dev-tools-native \
    libxfce4util \
    libxfce4ui \
    xfconf \
    \
    gstreamer1.0-plugins-base \
    taglib \
"

SRC_URI[sha256sum] = "6625288b760d38a15c295051ecf66c556fcad21dd1516d6d661c2a582421ee0e"

RDEPENDS:${PN} += "gstreamer1.0-plugins-good"

EXTRA_OECONF = "--disable-gtk-doc DATADIRNAME=share"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[clutter] = "--enable-clutter, --disable-clutter, clutter-gtk-1.0"
PACKAGECONFIG[notify] = "--enable-notify-plugin, --disable-notify-plugin, libnotify"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${libdir}/parole-0/*.so \
"
